package com.care.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.care.config.ComponentParser;
import com.care.config.InputParser;
import com.care.config.OutputParser;
import com.care.datatype.Component;
import com.care.datatype.ComponentLoadType;
import com.care.datatype.GenerateOutputType;
import com.care.datatype.Input;
import com.care.datatype.InputType;
import com.care.datatype.Output;
import com.care.datatype.ParseInputType;
import com.care.exception.ComponentException;
import com.care.exception.ConfigException;
import com.care.exception.InputOutputException;
import com.care.exception.PlatformException;
import com.care.html.HTMLGenerator;
import com.care.platform.InputHandler;
import com.care.platform.OutputHandler;
import com.care.platform.PlatformManager;

// TODO error checking of all strings required
// TODO handle constant string properly in some library
public class Main
{
    private static Input input;
    private static List<Component> components;
    private static Output output;

    /**
     * Parses config file to Input, Component and Output objects
     * 
     * @param configFilePath
     * @throws ConfigException
     */
    private static void ParseConfig(String configFilePath) throws ConfigException
    {
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(configFilePath));

            components = new ArrayList<Component>();

            // Iterating through the nodes and extracting the data.
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Node node = nodeList.item(i);

                // parse input
                if (node.getNodeName().equalsIgnoreCase("input"))
                {
                    input = InputParser.GetInput(node);
                    continue;
                }

                // parse main component
                if (node.getNodeName().equalsIgnoreCase("components"))
                {
                    NodeList componentList = node.getChildNodes();
                    for (int j = 0; j < componentList.getLength(); j++)
                    {
                        Node componentNode = componentList.item(j);
                        if (componentNode.getNodeName().equalsIgnoreCase("component"))
                        {
                            components.add(ComponentParser.GetComponent(componentNode));
                        }
                    }
                    continue;
                }

                // parse the output
                if (node.getNodeName().equalsIgnoreCase("output"))
                {
                    output = OutputParser.GetOutput(node);
                    continue;
                }
            }
        }
        catch (Exception e)
        {
            // TODO log stack trace
            throw new ConfigException(e.getMessage());
        }
    }

    /**
     * Parses input file from file to object
     * 
     * @return
     * @throws InputOutputException
     */
    private static Object ParseInputFile() throws InputOutputException
    {
        try
        {
            InputHandler inputHandler = new InputHandler(input);

            String inputContent = inputHandler.ReadFile();
            if (input.getParseType() == ParseInputType.LIST)
            {
                return inputHandler.ConvertXmlStringToList(inputContent);
            }
            else
            {
                return inputContent;
            }
        }
        catch (Exception e)
        {
            // TODO log stack trace
            throw new InputOutputException(e.getMessage());
        }
    }

    /**
     * Calls component with proper function and returns the list returned by the
     * function
     * 
     * @param inputContent
     * @return
     * @throws PlatformException
     * @throws ComponentException
     */
    private static List<String> StartPlatform(Object inputContent, String outputFolderPath) throws PlatformException, ComponentException
    {
        try
        {
            PlatformManager manager = new PlatformManager();
            List<String> outputContent = null;

            for (int i = 0; i < components.size(); i++)
            {
                // Loads component class
                Component component = components.get(i);
                if (component.getLoadType() == ComponentLoadType.CLASS)
                {
                    manager.InitializeClassComponent(component);
                }
                else if (component.getLoadType() == ComponentLoadType.JAR)
                {
                    manager.InitializeJarComponent(component);
                }

                // Calls function accordingly
                if (i == 0 && input.getParseType() == ParseInputType.STRING)
                {
                    outputContent = manager.DoWork((String) inputContent, outputFolderPath);
                }
                else if (i == 0 && input.getParseType() == ParseInputType.LIST)
                {
                    outputContent = manager.DoWork((List<String>) inputContent, outputFolderPath);
                }
                else
                {
                    outputContent = manager.DoWork((List<String>) outputContent, outputFolderPath);
                }
            }

            return outputContent;
        }
        catch (ComponentException e)
        {
            // TODO log stack trace
            throw e;
        }
        catch (Exception e)
        {
            // TODO log stack trace
            throw new PlatformException(e);
        }
    }

    /**
     * Generates output file from List<String>
     * 
     * @param outputContent
     * @throws InputOutputException
     */
    private static void GenerateOutputFile(List<String> outputContent) throws InputOutputException
    {
        try
        {
            OutputHandler outputHandler = new OutputHandler(output);

            if (output.getGenerateType().equals(GenerateOutputType.LIST))
            {
                outputHandler.WriteListToFileAsXml(outputContent);
            }
            else if (output.getGenerateType().equals(GenerateOutputType.STRING))
            {
                outputHandler.WriteListToFileAsString(outputContent);
            }
        }
        catch (Exception e)
        {
            // TODO log stack trace
            throw new InputOutputException(e);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            if (args.length < 1)
            {
                throw new IllegalArgumentException("Config file path required as system argument");
            }

            String configFilePath = args[0];

            execute(configFilePath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // TODO properly display exception message and stack
            // trace somewhere
        }
    }

    public static void execute(String configFilePath) throws Exception
    {
        ParseConfig(configFilePath);

        if (input.getType() == InputType.FOLDER)
        {
            List<String> fileNames = Helper.GetFileNames(input.getPath());
            File inputFolderPath = new File(input.getPath());
            File outputFolderPath = new File(output.getPath());

            for (String filename : fileNames)
            {
                // Get the input from the format
                File inputFile = new File(inputFolderPath, filename);
                input.setPath(inputFile.getPath());
                Object content = ParseInputFile();

                // Starting the platform
                List<String> stringList = StartPlatform(content, output.getPath());

                // Write the output in the required format
                File outputFile = new File(outputFolderPath, filename);
                output.setPath(outputFile.getPath());
                
                GenerateOutputFile(stringList);
                GenerateHTMLOutputFile(outputFile.getPath());
            }
            
            System.out.println("Output and HTML File generated!!");
        }
        else
        {
            // Get the input from the format
            Object content = ParseInputFile();

            // Starting the platform
            List<String> outputList = StartPlatform(content, output.getPath());

            // Write the output in the required format
            GenerateOutputFile(outputList);
            System.out.println("Output File generated!!");
            
            GenerateHTMLOutputFile(output.getPath());
            System.out.println("HTML File generated!!");
        }

    }

    private static void GenerateHTMLOutputFile(String path) throws SAXException, IOException, ParserConfigurationException
    {
        // TODO changed below html function
        String htmlContent = HTMLGenerator.getDiffHTML(path);
        PrintWriter writer = new PrintWriter(Helper.GetFolderName(path)+"/"+Helper.GetFileName(path)+".html");
        writer.println(htmlContent);
        writer.close();
    }
}
