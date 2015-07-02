package com.care.gui;

/**
 * This class is used to create the Add/Configure Component dialog box.
 * @author Suryansh Raj
 * @since 11-06-2015
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.care.datatype.Component;
import com.care.datatype.ComponentLoadType;
import com.care.datatype.ComponentType;
import com.google.common.base.Strings;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.Color;

public class ComponentPanel extends JPanel
{
	
	// Swing components used in the layout designing
    private JFrame parentFrame;
    private JTextField classPathTextField, jarPathTextField, classNameTextField;
    private JPanel mainPanel, buttonsPanel, moduleTypePanel, componentTypePanel, subComponentTypePanel, loadTypePanel, classPathPanel, jarPathPanel, classNamePanel;
    private JComboBox moduleTypeComboBox, componentTypeComboBox, subComponentTypeComboBox, loadTypeComboBox;
    private JButton classPathBrowseButton, jarPathBrowseButton, loadButton, saveButton;
    private final JFileChooser fileChooser = new JFileChooser();
    
    // Component Load type and Component type
    final ComponentLoadType[] componentLoadSupport = ComponentLoadType.values();
    final ComponentType[] componentTypeSupport = ComponentType.values();
    
    /**
     * This is the default constructor for ComponentPanel class. It sets up the GUI of Add/Configure Component dialog box
     * @param contentFrame - The parent window executing the dialog box
	 * @exception Nothing
     */
    public ComponentPanel(JFrame contentFrame)
    {
    	
        parentFrame = contentFrame;
        
        setLayout(new FormLayout(new ColumnSpec[] {
        		FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        		ColumnSpec.decode("pref:grow"),
        		FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        		FormFactory.DEFAULT_COLSPEC,
        		FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
        	new RowSpec[] {
        		FormFactory.LINE_GAP_ROWSPEC,
        		RowSpec.decode("pref:grow"),
        		FormFactory.LINE_GAP_ROWSPEC,}));
        
        mainPanel = new JPanel();
        add(mainPanel, "2, 2, fill, fill");
        mainPanel.setLayout(new FormLayout(new ColumnSpec[] {
        		FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        		ColumnSpec.decode("pref:grow"),
        		FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
        	new RowSpec[] {
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,}));
        
        moduleTypePanel = new JPanel();
        moduleTypePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Module", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainPanel.add(moduleTypePanel, "2, 2, fill, fill");
        moduleTypePanel.setLayout(new FormLayout(new ColumnSpec[] {
        		ColumnSpec.decode("pref:grow"),},
        	new RowSpec[] {
        		FormFactory.DEFAULT_ROWSPEC,}));
        
        moduleTypeComboBox = new JComboBox();
        moduleTypeComboBox.setEnabled(false);
        moduleTypePanel.add(moduleTypeComboBox, "1, 1, fill, default");
        
        componentTypePanel = new JPanel();
        componentTypePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Component", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainPanel.add(componentTypePanel, "2, 4, fill, fill");
        componentTypePanel.setLayout(new FormLayout(new ColumnSpec[] {
        		ColumnSpec.decode("pref:grow"),},
        	new RowSpec[] {
        		FormFactory.DEFAULT_ROWSPEC,}));
        
        componentTypeComboBox = new JComboBox(componentTypeSupport);
        componentTypePanel.add(componentTypeComboBox, "1, 1, fill, default");
        
        subComponentTypePanel = new JPanel();
        subComponentTypePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Sub-Component", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainPanel.add(subComponentTypePanel, "2, 6, fill, fill");
        subComponentTypePanel.setLayout(new FormLayout(new ColumnSpec[] {
        		ColumnSpec.decode("pref:grow"),},
        	new RowSpec[] {
        		FormFactory.DEFAULT_ROWSPEC,}));
        
        subComponentTypeComboBox = new JComboBox();
        subComponentTypeComboBox.setEnabled(false);
        subComponentTypePanel.add(subComponentTypeComboBox, "1, 1, fill, default");
        
        loadTypePanel = new JPanel();
        loadTypePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Load Type", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainPanel.add(loadTypePanel, "2, 8, fill, fill");
        loadTypePanel.setLayout(new FormLayout(new ColumnSpec[] {
        		ColumnSpec.decode("pref:grow"),},
        	new RowSpec[] {
        		FormFactory.DEFAULT_ROWSPEC,}));
        
        loadTypeComboBox = new JComboBox(componentLoadSupport);
        loadTypePanel.add(loadTypeComboBox, "1, 1, fill, default");
        
        classPathPanel = new JPanel();
        classPathPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Class Path Folder", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainPanel.add(classPathPanel, "2, 10, fill, fill");
        classPathPanel.setLayout(new FormLayout(new ColumnSpec[] {
        		FormFactory.DEFAULT_COLSPEC,
        		FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        		ColumnSpec.decode("pref:grow"),},
        	new RowSpec[] {
        		FormFactory.DEFAULT_ROWSPEC,}));
        
        classPathBrowseButton = new JButton("Browse");
        classPathBrowseButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(parentFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fileChooser.getSelectedFile();
                    classPathTextField.setText(file.getAbsolutePath());
                }
                else
                {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });
        classPathPanel.add(classPathBrowseButton, "1, 1");
        
        classPathTextField = new JTextField();
        classPathPanel.add(classPathTextField, "3, 1, fill, default");
        classPathTextField.setColumns(10);
        
        jarPathPanel = new JPanel();
        jarPathPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Jar Path Folder", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainPanel.add(jarPathPanel, "2, 12, fill, fill");
        jarPathPanel.setLayout(new FormLayout(new ColumnSpec[] {
        		FormFactory.DEFAULT_COLSPEC,
        		FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        		ColumnSpec.decode("pref:grow"),},
        	new RowSpec[] {
        		FormFactory.DEFAULT_ROWSPEC,}));
        
        jarPathBrowseButton = new JButton("Browse");
        jarPathBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = fileChooser.showOpenDialog(parentFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    jarPathTextField.setText(file.getAbsolutePath());
                }
                else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });
        jarPathPanel.add(jarPathBrowseButton, "1, 1");
        
        jarPathTextField = new JTextField();
        jarPathPanel.add(jarPathTextField, "3, 1, fill, default");
        jarPathTextField.setColumns(20);
        
        classNamePanel = new JPanel();
        classNamePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Class Name", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        mainPanel.add(classNamePanel, "2, 14, fill, fill");
        classNamePanel.setLayout(new FormLayout(new ColumnSpec[] {
        		ColumnSpec.decode("pref:grow"),},
        	new RowSpec[] {
        		FormFactory.DEFAULT_ROWSPEC,}));
        
        classNameTextField = new JTextField();
        classNamePanel.add(classNameTextField, "1, 1, fill, default");
        classNameTextField.setColumns(20);
        
        buttonsPanel = new JPanel();
        add(buttonsPanel, "4, 2, fill, fill");
        buttonsPanel.setLayout(new FormLayout(new ColumnSpec[] {
        		FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        		FormFactory.DEFAULT_COLSPEC,
        		FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
        	new RowSpec[] {
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,
        		FormFactory.DEFAULT_ROWSPEC,
        		FormFactory.LINE_GAP_ROWSPEC,}));
        
        loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				try {
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int returnVal = fileChooser.showOpenDialog(parentFrame);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						loadComponent(file);
					}
				}
				catch(IOException exception) {
					
				}
				catch(SAXException exception) {
					
				}
				catch(ParserConfigurationException exception) {
					
				}
        	}
        });
        buttonsPanel.add(loadButton, "2, 2");
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				String xml;
                try {
                    xml = saveComponent();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnVal = fileChooser.showSaveDialog(parentFrame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        if(file.getName().length()>4) {
                        	String extension = file.getName().substring(file.getName().length()-4);
                       		if(extension.equalsIgnoreCase(".xml")) {
                       			PrintWriter writer = new PrintWriter(file, "UTF-8");
                       			writer.println(xml);
                        		writer.close();
                        	}
                        	else {
                        		PrintWriter writer = new PrintWriter(file+".xml", "UTF-8");
                        		writer.println(xml);
                        		writer.close();
                        	}
                        }
                        else {
                        	PrintWriter writer = new PrintWriter(file+".xml", "UTF-8");
                    		writer.println(xml);
                    		writer.close();
                        }
                    }
                }
                catch (IOException exception) {
                	
                }
        	}
        });
        buttonsPanel.add(saveButton, "2, 4");

    }

    /**
     * This method is used to return the component data to Main Window from Add/Configure Component dialog box.
     * @param Nothing
     * @return Component - The component data returned upon clicking OK button in Add/Configure Component dialog box
     * @exception Nothing
     */
    public Component getComponent()
    {
        Component component = new Component();
        component.setClassName(classNameTextField.getText());
        component.setLoadType((ComponentLoadType) loadTypeComboBox.getSelectedItem());
        component.setType((ComponentType) componentTypeComboBox.getSelectedItem());
        if (component.getLoadType() == ComponentLoadType.CLASS) {
            component.setPath(classPathTextField.getText());
            component.setDependencyPath(jarPathTextField.getText());
        }
        else {
            component.setPath(jarPathTextField.getText());
        }
        return component;
    }
    
    /**
     * This method is used to load data of an already added component from Main Window to Configure Component dialog box.
     * @param {Component} component - The component data to be loaded on selecting Configure Component option in Main Window
     * @return Nothing
     * @exception Nothing
     */
    public void setComponent(Component component)
    {
    	classNameTextField.setText(component.getClassName());
    	loadTypeComboBox.setSelectedItem(component.getLoadType());
    	componentTypeComboBox.setSelectedItem(component.getType());
        if (component.getLoadType() == ComponentLoadType.CLASS) {
        	classPathTextField.setText(component.getPath());
        	jarPathTextField.setText(component.getDependencyPath());
        }
        else {
        	jarPathTextField.setText(component.getDependencyPath());
        }
    }
    
    /**
	 * This method is used during Save option to create the XML file containing the component data to be saved.
	 * @param Nothing
	 * @return String - XML file created for saving component
	 * @throws IOException
	 */
	protected String saveComponent() throws IOException {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<component>");
        xmlBuilder.append("<loadType>");
        xmlBuilder.append(loadTypeComboBox.getSelectedItem().toString());
        xmlBuilder.append("</loadType>");
        xmlBuilder.append("<type>");
        xmlBuilder.append(componentTypeComboBox.getSelectedItem().toString());
        xmlBuilder.append("</type>");
        xmlBuilder.append("<path>");
        xmlBuilder.append(classPathTextField.getText());
        xmlBuilder.append("</path>");
        if (!Strings.isNullOrEmpty(jarPathTextField.getText()))
        {
            xmlBuilder.append("<dependencyPath>");
            xmlBuilder.append(jarPathTextField.getText());
            xmlBuilder.append("</dependencyPath>");
        }
        xmlBuilder.append("<className>");
        xmlBuilder.append(classNameTextField.getText());
        xmlBuilder.append("</className>");
        xmlBuilder.append("</component>");
        return xmlBuilder.toString();
    }

	/**
	 * This method is used during Load option to read and process the XML file containing details of a saved component.
	 * @param {File} file - XML file containing saved details of a pipeline
	 * @return Nothing
	 * @throws IOException, SAXException, ParserConfigurationException
	 */
	protected void loadComponent(File file) throws IOException, SAXException, ParserConfigurationException  {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);
        doc.getDocumentElement().normalize();
        NodeList componentList = doc.getElementsByTagName("component");
        int totalComponents = componentList.getLength();
        Node currentComponent;
        Element currentElement, currentSubElement;
        NodeList currentList;
        currentComponent = componentList.item(0);
    	if(currentComponent.getNodeType() == Node.ELEMENT_NODE) {
    		currentElement = (Element) currentComponent;
    		currentList = currentElement.getElementsByTagName("loadType");
    		if(currentList.getLength()!=0) {
            	currentSubElement = (Element)currentList.item(0);
            	currentList = currentSubElement.getChildNodes();
            	if(((Node)currentList.item(0)).getNodeValue().trim().equals("CLASS")) 
            		loadTypeComboBox.setSelectedItem(ComponentLoadType.CLASS);
            	else if(((Node)currentList.item(0)).getNodeValue().trim().equals("JAR")) 
            		loadTypeComboBox.setSelectedItem(ComponentLoadType.JAR);
    		}
    		currentList = currentElement.getElementsByTagName("type");
    		if(currentList.getLength()!=0) {
            	currentSubElement = (Element)currentList.item(0);
            	currentList = currentSubElement.getChildNodes();
            	if(((Node)currentList.item(0)).getNodeValue().trim().equals("CONCEPT_EXTRACTOR")) 
            		componentTypeComboBox.setSelectedItem(ComponentType.CONCEPT_EXTRACTOR);
            	else if(((Node)currentList.item(0)).getNodeValue().trim().equals("DE_IDENTIFIER")) 
            		componentTypeComboBox.setSelectedItem(ComponentType.DE_IDENTIFIER);
            	if(((Node)currentList.item(0)).getNodeValue().trim().equals("DICTIONARY_BUILDER")) 
            		componentTypeComboBox.setSelectedItem(ComponentType.DICTIONARY_BUILDER);
            	else if(((Node)currentList.item(0)).getNodeValue().trim().equals("PRE_PROCESSOR")) 
            		componentTypeComboBox.setSelectedItem(ComponentType.PRE_PROCESSOR);
            	if(((Node)currentList.item(0)).getNodeValue().trim().equals("STEMMER")) 
            		componentTypeComboBox.setSelectedItem(ComponentType.STEMMER);
            	else if(((Node)currentList.item(0)).getNodeValue().trim().equals("STOP_WORD_REMOVER")) 
            		componentTypeComboBox.setSelectedItem(ComponentType.STOP_WORD_REMOVER);
            	if(((Node)currentList.item(0)).getNodeValue().trim().equals("TOKENIZER")) 
            		componentTypeComboBox.setSelectedItem(ComponentType.TOKENIZER);
    		}
    		currentList = currentElement.getElementsByTagName("path");
    		if(currentList.getLength()!=0) {
    			currentSubElement = (Element)currentList.item(0);
    			currentList = currentSubElement.getChildNodes();
    			classPathTextField.setText(((Node)currentList.item(0)).getNodeValue().trim());
    		}
    		currentList = currentElement.getElementsByTagName("dependencyPath");
    		if(currentList.getLength()!=0) {
    			currentSubElement = (Element)currentList.item(0);
            	currentList = currentSubElement.getChildNodes();
            	jarPathTextField.setText(((Node)currentList.item(0)).getNodeValue().trim());
    		}
    		currentList = currentElement.getElementsByTagName("className");
    		if(currentList.getLength()!=0) {
            	currentSubElement = (Element)currentList.item(0);
            	currentList = currentSubElement.getChildNodes();
            	classNameTextField.setText(((Node)currentList.item(0)).getNodeValue().trim());
    		}
    	}
    }
    
}
