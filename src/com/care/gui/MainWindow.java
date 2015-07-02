/**
 * 
 */
package com.care.gui;

/**
 * This class is used to create the GUI of the software
 * @author Suryansh Raj
 * @since 01-06-2015
 */

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.care.datatype.Component;
import com.care.datatype.ComponentLoadType;
import com.care.datatype.ComponentType;
import com.care.main.Main;
import com.google.common.base.Strings;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MainWindow {
	
	// The name of configuration file which is executed
	private final String DEFAULT_SAVE_NAME = "config_%s.xml";
	
	// Swing components used in the layout designing
	private JFrame contentFrame;
	private Dimension minimumSize;
	private JScrollPane contentScrollPane;
	private JPanel contentPanel, inputPanel, pipelinePanel, outputPanel, statusPanel, pipelineButtonsVerticalPanel, pipelineButtonsHorizontalPanel;
	private JTextField inputPath, outputPath;
	private JTextArea inputTextArea, outputTextArea;
	private JButton inputBrowse, inputPaste;
	private JButton pipelineMoveUp, pipelineMoveDown, pipelineSave, pipelineLoad, pipelineAdd, pipelineRemove, pipelineConfigure, pipelineExecute;
	private JButton outputBrowse, outputShow;
	private JComboBox inputFileType, outputFileType;
	private JList pipelineList;
	private JTextPane inputStatusTextPane, pipelineStatusTextPane, outputStatusTextPane;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, pipelineMenu, helpMenu, menuRecentlyUsedInput;
	private JMenuItem menuItemNewText, menuItemSelectInputPath, menuItemSelectOutputPath, menuItemExit;
	private JMenuItem menuItemCut, menuItemCopy, menuItemPaste;
	private JMenuItem menuItemExecute, menuItemShowCurrentConfigurationFile;
	private JMenuItem menuItemAddComponent, menuItemRemoveComponent, menuItemConfigureComponent;
	private JMenuItem menuItemMoveUp, menuItemMoveDown; 
	private JMenuItem menuItemSavePipeline, menuItemLoadPipeline;
	private JMenuItem menuItemHelp, menuItemAbout;
	private JSeparator separatorFileMenu1, separatorFileMenu2, separatorPipelineMenu1, separatorPipelineMenu2, separatorPipelineMenu3;
	private final JFileChooser fileChooser = new JFileChooser();
	private JScrollPane inputScrollPane;
	private JScrollPane outputScrollPane;
	private JScrollPane pipelineScrollPane;
	
	// List model to display contents of pipeline, used in pipelineList variable
	private DefaultListModel<Component> componentListModel = new DefaultListModel();
	
	// Stores configuration file path
	private String configFilePath;

	/**
	 * This is the default constructor for MainWindow class. It sets up the complete GUI of the software.
	 * @param Nothing
	 * @exception Nothing 
	 */
	MainWindow() {
		
		// Complete window frame of the GUI
		contentFrame = new JFrame("Clinical Data Extraction");
		contentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentFrame.setBackground(new Color(192, 192, 192));
		
		// Dimensions of the complete window
		contentFrame.setSize(900, 720);
		minimumSize = new Dimension(660, 528);
		contentFrame.setMinimumSize(minimumSize);
		
		contentFrame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("pref:grow"),},
			new RowSpec[] {
				RowSpec.decode("pref:grow"),}));
		
		// Scroll bars activated for the complete layout when required
		contentScrollPane = new JScrollPane();
		contentFrame.getContentPane().add(contentScrollPane, "1, 1, fill, fill");
		
		// Panel containing input panel, output panel, pipeline panel and status bar panel
		contentPanel = new JPanel();
		contentScrollPane.setViewportView(contentPanel);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("pref:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("pref:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		// Input panel
		inputPanel = new JPanel();
		inputPanel.setBorder(new TitledBorder(null, "Input", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(inputPanel, "2, 2, 1, 3, fill, fill");
		inputPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("pref:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("pref:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		inputScrollPane = new JScrollPane();
		inputScrollPane.setBorder(null);
		inputPanel.add(inputScrollPane, "2, 2, 3, 1, fill, fill");
		
		inputTextArea = new JTextArea();
		inputScrollPane.setViewportView(inputTextArea);
		inputScrollPane.setPreferredSize(inputTextArea.getSize());
		
		inputBrowse = new JButton(" Select Input");
		inputBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fileChooser.showOpenDialog(contentFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                	inputTextArea.setText("");
                    File file = fileChooser.getSelectedFile();
                    try {
            			BufferedReader br = new BufferedReader(new FileReader(file));
                		String input;
            			while((input=br.readLine()) != null){
            				inputTextArea.append(input.toString()+"\n");
            			}
            			br.close();
            			outputTextArea.append("Input selected is a file.\n");
            		}
            		catch(FileNotFoundException exception) {
            			outputTextArea.append("Input selected is a folder.\n");
            		}
            		catch(IOException exception){
            			outputTextArea.append("File can not be read.\n");
            		}
            		inputPath.setText(file.getAbsolutePath());
            		inputStatusTextPane.setText("Input Ready");
                }
                else {
                    outputTextArea.append("Open command cancelled by user.\n");
                }
			}
		});
		inputPanel.add(inputBrowse, "2, 4, fill, fill");
		
		inputPath = new JTextField();
		inputPath.setBorder(new EmptyBorder(4, 2, 4, 2));
		inputPanel.add(inputPath, "4, 4, fill, fill");
		inputPath.setColumns(10);
		
		inputPaste = new JButton(new DefaultEditorKit.PasteAction());
		inputPaste.setText("Paste");
		inputPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String fileName = "temporaryInput.txt";
					File file = new File(fileName);
					PrintWriter writer = new PrintWriter(file, "UTF-8");
					writer.println(inputTextArea.getText());
					writer.close();
					inputPath.setText(file.getAbsolutePath());
				}
				catch(IOException exception) {
					outputTextArea.append("Temporary input file upon Paste option could not be created.\n");
				}
			}
		});
		inputPanel.add(inputPaste, "2, 6, fill, fill");
		
		inputFileType = new JComboBox();
		inputFileType.addItem("STRING");
		inputFileType.addItem("LIST");
		inputPanel.add(inputFileType, "4, 6, fill, fill");
		
		// Pipeline panel
		pipelinePanel = new JPanel();
		pipelinePanel.setBorder(new TitledBorder(null, "Pipeline", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(pipelinePanel, "4, 2, fill, fill");
		pipelinePanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("pref:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		pipelineScrollPane = new JScrollPane();
		pipelineScrollPane.setBorder(null);
		pipelinePanel.add(pipelineScrollPane, "2, 2, fill, fill");
		
		pipelineList = new JList(componentListModel);
		pipelineList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
	            if (pipelineList.getSelectedIndex() == -1) {
	                pipelineRemove.setEnabled(false);
	                menuItemRemoveComponent.setEnabled(false);
	                pipelineMoveUp.setEnabled(false);
	                menuItemMoveUp.setEnabled(false);
	                pipelineMoveDown.setEnabled(false);
	                menuItemMoveDown.setEnabled(false);
	                pipelineConfigure.setEnabled(false);
	                menuItemConfigureComponent.setEnabled(false);
	            }
	            else {
	                pipelineRemove.setEnabled(true);
	                menuItemRemoveComponent.setEnabled(true);
	                pipelineConfigure.setEnabled(true);
	                menuItemConfigureComponent.setEnabled(true);
	                if(pipelineList.getSelectedIndex() > 0) {
	                	pipelineMoveUp.setEnabled(true);
                		menuItemMoveUp.setEnabled(true);
	                }
	                else {
	                	pipelineMoveUp.setEnabled(false);
                		menuItemMoveUp.setEnabled(false);
	                }
	                if(pipelineList.getSelectedIndex() < (pipelineList.getModel().getSize()-1)) {
	                	pipelineMoveDown.setEnabled(true);
                		menuItemMoveDown.setEnabled(true);
	                }
	                else {
	                	pipelineMoveDown.setEnabled(false);
                		menuItemMoveDown.setEnabled(false);
	                }
	            }
			}
		});
		pipelineScrollPane.setViewportView(pipelineList);
		pipelineScrollPane.setPreferredSize(pipelineList.getSize());
		
		pipelineButtonsVerticalPanel = new JPanel();
		pipelineButtonsVerticalPanel.setBorder(null);
		pipelinePanel.add(pipelineButtonsVerticalPanel, "4, 2, fill, fill");
		pipelineButtonsVerticalPanel.setLayout(new FormLayout(new ColumnSpec[] {
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
		
		pipelineMoveUp = new JButton("Move Up");
		pipelineMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                int currentPosition = pipelineList.getSelectedIndex();
                if(currentPosition > 0) {
                	Component currentComponent = componentListModel.getElementAt(currentPosition);
                	Component previousComponent = componentListModel.getElementAt(currentPosition-1);
                	componentListModel.set(currentPosition, previousComponent);
                	componentListModel.set(currentPosition-1, currentComponent);
                	pipelineList.setSelectedIndex(currentPosition-1);
                	pipelineList.ensureIndexIsVisible(currentPosition-1);
                	if(currentPosition-1 == 0) {
                		pipelineMoveUp.setEnabled(false);
                		menuItemMoveUp.setEnabled(false);
                	}
                }
			}
		});
		pipelineMoveUp.setEnabled(false);
		pipelineButtonsVerticalPanel.add(pipelineMoveUp, "2, 2, fill, fill");
		
		pipelineMoveDown = new JButton("Move Down");
		pipelineMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int currentPosition = pipelineList.getSelectedIndex();
                if(currentPosition < (pipelineList.getModel().getSize()-1)) {
                	Component currentComponent = componentListModel.getElementAt(currentPosition);
                	Component nextComponent = componentListModel.getElementAt(currentPosition+1);
                	componentListModel.set(currentPosition, nextComponent);
                	componentListModel.set(currentPosition+1, currentComponent);
                	pipelineList.setSelectedIndex(currentPosition+1);
                	pipelineList.ensureIndexIsVisible(currentPosition+1);
                	if(currentPosition+1 == (pipelineList.getModel().getSize()-1)) {
                		pipelineMoveDown.setEnabled(false);
                		menuItemMoveDown.setEnabled(false);
                	}
                }
			}
		});
		pipelineMoveDown.setEnabled(false);
		pipelineButtonsVerticalPanel.add(pipelineMoveDown, "2, 4, fill, fill");
		
		pipelineSave = new JButton("Save");
		pipelineSave.setEnabled(false);
		pipelineSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String xml;
                try {
                    xml = savePipeline();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnVal = fileChooser.showSaveDialog(contentFrame);
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
                    else {
                        outputTextArea.append("Save pipeline command cancelled by user.\n");
                    }
                    outputTextArea.append("Pipeline saved successfully. \n");
                }
                catch (IOException exception) {
                	outputTextArea.append("Pipeline saved failed.\n");
                }
			}
		});
		pipelineButtonsVerticalPanel.add(pipelineSave, "2, 6, fill, fill");
		
		pipelineLoad = new JButton("Load");
		pipelineLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int returnVal = fileChooser.showOpenDialog(contentFrame);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						Component components[] = loadPipeline(file);
						componentListModel.removeAllElements();
						for(int i=0; i<components.length; i++) 
		                    componentListModel.addElement(components[i]);
					}
					else {
						outputTextArea.append("Load pipeline command cancelled by user.\n");
					}
				}
				catch(IOException exception) {
					outputTextArea.append("Pipeline load failed.\n");
				}
				catch(SAXException exception) {
					outputTextArea.append("Error in selected XML file.\n");
				}
				catch(ParserConfigurationException exception) {
					outputTextArea.append("Parsing of XML file failed.\n");
				}
				if(pipelineList.getModel().getSize()!=0) {
                	pipelineExecute.setEnabled(true);
                	menuItemExecute.setEnabled(true);
                	pipelineSave.setEnabled(true);
                	menuItemSavePipeline.setEnabled(true);
                	pipelineStatusTextPane.setText("Pipeline Ready");
                }
                else {
                	pipelineStatusTextPane.setText("Pipeline Not Ready");
                }
			}
		});
		pipelineButtonsVerticalPanel.add(pipelineLoad, "2, 8, fill, fill");
		
		pipelineButtonsHorizontalPanel = new JPanel();
		pipelineButtonsHorizontalPanel.setBorder(null);
		pipelinePanel.add(pipelineButtonsHorizontalPanel, "2, 4, 3, 1, fill, fill");
		pipelineButtonsHorizontalPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		pipelineAdd = new JButton("Add");
		pipelineAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                ComponentPanel panel = new ComponentPanel(contentFrame);
                int choice = JOptionPane.showOptionDialog(contentFrame, panel, "Add Component", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (choice == JOptionPane.OK_OPTION) {
                    Component component = panel.getComponent();
                    componentListModel.addElement(component);
                }
                if(pipelineList.getModel().getSize()!=0) {
                	pipelineExecute.setEnabled(true);
                	menuItemExecute.setEnabled(true);
                	pipelineSave.setEnabled(true);
                	menuItemSavePipeline.setEnabled(true);
                	pipelineStatusTextPane.setText("Pipeline Ready");
                }
                else {
                	pipelineStatusTextPane.setText("Pipeline Not Ready");
                }
			}
		});
		pipelineButtonsHorizontalPanel.add(pipelineAdd, "2, 2, fill, fill");
		
		pipelineRemove = new JButton("Remove");
		pipelineRemove.setEnabled(false);
		pipelineRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                while (pipelineList.getSelectedIndex() != -1) {
                    componentListModel.remove(pipelineList.getSelectedIndex());
                }
                if(pipelineList.getModel().getSize()==0) {
                	pipelineExecute.setEnabled(false);
                	menuItemExecute.setEnabled(false);
                	pipelineSave.setEnabled(false);
                	menuItemSavePipeline.setEnabled(false);
                	pipelineStatusTextPane.setText("Pipeline Not Ready");
                }
                else {
                	pipelineStatusTextPane.setText("Pipeline Ready");
                }
            }
        });
		pipelineButtonsHorizontalPanel.add(pipelineRemove, "4, 2, fill, fill");
		
		pipelineConfigure = new JButton("Configure");
		pipelineConfigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = pipelineList.getSelectedIndex();
				Component component = (Component) componentListModel.getElementAt(index);
                ComponentPanel panel = new ComponentPanel(contentFrame);
                panel.setComponent(component);
                int choice = JOptionPane.showOptionDialog(contentFrame, panel, "Configure Component", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (choice == JOptionPane.OK_OPTION) {
                    component = panel.getComponent();
                    componentListModel.removeElementAt(index);
                    componentListModel.insertElementAt(component, index);
                    pipelineList.setSelectedIndex(index);
                }
                if(pipelineList.getModel().getSize()!=0) {
                	pipelineExecute.setEnabled(true);
                	menuItemExecute.setEnabled(true);
                	pipelineSave.setEnabled(true);
                	menuItemSavePipeline.setEnabled(true);
                	pipelineStatusTextPane.setText("Pipeline Ready");
                }
                else {
                	pipelineStatusTextPane.setText("Pipeline Not Ready");
                }
			}
		});
		pipelineConfigure.setEnabled(false);
		pipelineButtonsHorizontalPanel.add(pipelineConfigure, "6, 2, fill, fill");
		
		pipelineExecute = new JButton("Execute");
		pipelineExecute.setEnabled(false);
		pipelineExecute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(inputPath.getText().length()==0&&inputTextArea.getText().length()==0) {
            		outputTextArea.append("No input provided.\n");
            		return;
            	}
            	if(inputPath.getText().length()==0&&inputTextArea.getText().length()>0) {
            		try {
    					String fileName = "temporaryInput.txt";
    					File file = new File(fileName);
    					PrintWriter writer = new PrintWriter(file, "UTF-8");
    					writer.println(inputTextArea.getText());
    					writer.close();
    					inputPath.setText(file.getAbsolutePath());
    				}
    				catch(IOException exception) {
    					outputTextArea.append("Temporary input file could not be created.\n");
    				}
            	}
            	if(outputPath.getText().length()==0) {
            		String fileName = inputPath.getText();
    				File file = new File(fileName);
    				if(file.isDirectory()) 
        				outputPath.setText(file.getAbsolutePath()+"\\Output");
    				else {
    					file = new File("temporaryOutput.txt");
    					outputPath.setText(file.getAbsolutePath());
    				}
            	}
                String xml;
                try {
                    xml = generateXML();
                    UUID uuid = UUID.randomUUID();
                    String configFileName = String.format(DEFAULT_SAVE_NAME, uuid.toString());
                    PrintWriter writer = new PrintWriter(configFileName, "UTF-8");
                    writer.println(xml);
                    writer.close();
                    File file = new File(configFileName);
                    configFilePath = file.getAbsolutePath();
                    menuItemShowCurrentConfigurationFile.setEnabled(true);
                    Main.execute(configFileName);
                    file.delete();
                    outputTextArea.append("Ouput Generated. \n");
                    outputShow.setEnabled(true);
                }
                catch(UnsupportedEncodingException exception) {
                	outputTextArea.append("Input file encoding not supported.\n");
                }
                catch (Exception exception) {
                	outputTextArea.append("Unknown error during processing.\n");
                }
            }
        });
		pipelineButtonsHorizontalPanel.add(pipelineExecute, "8, 2, fill, fill");
		
		// Output panel
		outputPanel = new JPanel();
		outputPanel.setBorder(new TitledBorder(null, "Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(outputPanel, "4, 4, fill, fill");
		outputPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("pref:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("pref:grow"),},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("pref:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		outputScrollPane = new JScrollPane();
		outputScrollPane.setBorder(null);
		outputPanel.add(outputScrollPane, "2, 2, 3, 1, fill, fill");
		
		outputTextArea = new JTextArea();
		outputScrollPane.setViewportView(outputTextArea);
		outputScrollPane.setPreferredSize(outputTextArea.getSize());
		outputTextArea.setEditable(false);
		
		outputBrowse = new JButton("Select Output");
		outputBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fileChooser.showOpenDialog(contentFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();try {
            			BufferedReader br = new BufferedReader(new FileReader(file));
            			if(br.readLine() != null);
            			br.close();
            			outputTextArea.append("Output set to a file.\n");
            		}
            		catch(FileNotFoundException exception) {
            			outputTextArea.append("Output set to a folder.\n");
            		}
            		catch(IOException exception){
            			outputTextArea.append("File can not be read.\n");
            		}
                    outputPath.setText(file.getAbsolutePath());
                    outputStatusTextPane.setText("Output Path Ready");
                }
                else {
                    outputTextArea.append("Open command cancelled by user.\n");
                }
			}
		});
		outputPanel.add(outputBrowse, "2, 4, fill, fill");
		
		outputPath = new JTextField();
		outputPath.setBorder(new EmptyBorder(4, 2, 4, 2));
		outputPanel.add(outputPath, "4, 4, fill, fill");
		outputPath.setColumns(10);
		
		outputShow = new JButton("Show in Explorer");
		outputShow.setEnabled(false);
		outputShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(outputPath.getText()));
				}
				catch(IOException exception) {
					outputTextArea.append("Output path unreachable.\n");
				}
			}
		});
		outputPanel.add(outputShow, "2, 6, fill, fill");
		
		outputFileType = new JComboBox();
		outputFileType.addItem("STRING");
		outputFileType.addItem("LIST");
		outputPanel.add(outputFileType, "4, 6, fill, fill");
		
		// Status Bar panel 
		statusPanel = new JPanel();
		contentPanel.add(statusPanel, "2, 6, 3, 1, fill, fill");
		statusPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("pref:grow"),
				ColumnSpec.decode("pref:grow"),},
			new RowSpec[] {
				RowSpec.decode("default:grow"),}));
		
		inputStatusTextPane = new JTextPane();
		inputStatusTextPane.setEditable(false);
		inputStatusTextPane.setBackground(new Color(245, 245, 245));
		inputStatusTextPane.setText("Input Not Ready");
		inputStatusTextPane.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(1, 2, 1, 2)));
		statusPanel.add(inputStatusTextPane, "1, 1, fill, fill");
		
		pipelineStatusTextPane = new JTextPane();
		pipelineStatusTextPane.setEditable(false);
		pipelineStatusTextPane.setBackground(new Color(245, 245, 245));
		pipelineStatusTextPane.setText("Pipeline Not Ready");
		pipelineStatusTextPane.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(1, 2, 1, 2)));
		statusPanel.add(pipelineStatusTextPane, "2, 1, fill, fill");
		
		outputStatusTextPane = new JTextPane();
		outputStatusTextPane.setEditable(false);
		outputStatusTextPane.setBackground(new Color(245, 245, 245));
		outputStatusTextPane.setText("Output Not Ready");
		outputStatusTextPane.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), new EmptyBorder(1, 2, 1, 2)));
		statusPanel.add(outputStatusTextPane, "3, 1, fill, fill");
		
		// Menu Bar
		menuBar = new JMenuBar();
		contentScrollPane.setColumnHeaderView(menuBar);
		
		// File Menu
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);
		
		menuItemNewText = new JMenuItem("New Text...");
		menuItemNewText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inputTextArea.setText("");
				inputFileType.setSelectedIndex(0);
				inputPath.setText("");
				outputTextArea.setText("");
				outputFileType.setSelectedIndex(0);
				outputPath.setText("");
				outputShow.setEnabled(false);
			}
		});
		menuItemNewText.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		menuItemNewText.setMnemonic(KeyEvent.VK_N);
		fileMenu.add(menuItemNewText);
		
		menuItemSelectInputPath = new JMenuItem("Select Input Path");
		menuItemSelectInputPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fileChooser.showOpenDialog(contentFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                	inputTextArea.setText("");
                    File file = fileChooser.getSelectedFile();
                    try {
            			BufferedReader br = new BufferedReader(new FileReader(file));
                		String input;
            			while((input=br.readLine()) != null){
            				inputTextArea.append(input.toString()+"\n");
            			}
            			br.close();
            			outputTextArea.append("Input selected is a file.\n");
            		}
            		catch(FileNotFoundException exception) {
            			outputTextArea.append("Input selected is a folder.\n");
            		}
            		catch(IOException exception){
            			outputTextArea.append("File can not be read.\n");
            		}
            		inputPath.setText(file.getAbsolutePath());
            		inputStatusTextPane.setText("Input Ready");
                }
                else {
                    outputTextArea.append("Open command cancelled by user.\n");
                }
			}
		});
		menuItemSelectInputPath.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		menuItemSelectInputPath.setMnemonic(KeyEvent.VK_I);
		fileMenu.add(menuItemSelectInputPath);
		
		menuItemSelectOutputPath = new JMenuItem("Select Output Path");
		menuItemSelectOutputPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fileChooser.showOpenDialog(contentFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();try {
            			BufferedReader br = new BufferedReader(new FileReader(file));
            			if(br.readLine() != null);
            			br.close();
            			outputTextArea.append("Output set to a file.\n");
            		}
            		catch(FileNotFoundException exception) {
            			outputTextArea.append("Output set to a folder.\n");
            		}
            		catch(IOException exception){
            			outputTextArea.append("File can not be read.\n");
            		}
                    outputPath.setText(file.getAbsolutePath());
                    outputStatusTextPane.setText("Output Path Ready");
                }
                else {
                    outputTextArea.append("Open command cancelled by user.\n");
                }
			}
		});
		menuItemSelectOutputPath.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuItemSelectOutputPath.setMnemonic(KeyEvent.VK_O);
		fileMenu.add(menuItemSelectOutputPath);
		
		separatorFileMenu1 = new JSeparator();
		fileMenu.add(separatorFileMenu1);
		
		menuRecentlyUsedInput = new JMenu("Recently Used Input Path");
		menuRecentlyUsedInput.setEnabled(false);
		menuRecentlyUsedInput.setMnemonic(KeyEvent.VK_R);
		fileMenu.add(menuRecentlyUsedInput);
		
		separatorFileMenu2 = new JSeparator();
		fileMenu.add(separatorFileMenu2);
		
		menuItemExit = new JMenuItem("Exit");
		menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		menuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                System.exit(0);
			}
		});
		menuItemExit.setMnemonic(KeyEvent.VK_E);
		fileMenu.add(menuItemExit);
		
		// Edit Menu
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		menuBar.add(editMenu);
		
		menuItemCut = new JMenuItem(new DefaultEditorKit.CutAction());
		menuItemCut.setText("Cut");
		menuItemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		menuItemCut.setMnemonic(KeyEvent.VK_T);
		editMenu.add(menuItemCut);
		
		menuItemCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
		menuItemCopy.setText("Copy");
		menuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		menuItemCopy.setMnemonic(KeyEvent.VK_C);
		editMenu.add(menuItemCopy);
		
		menuItemPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
		menuItemPaste.setText("Paste");
		menuItemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		menuItemPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String fileName = "temporaryInput.txt";
					File file = new File(fileName);
					PrintWriter writer = new PrintWriter(file, "UTF-8");
					writer.println(inputTextArea.getText());
					writer.close();
					inputPath.setText(file.getAbsolutePath());
				}
				catch(IOException exception) {
					outputTextArea.append("Temporary input file could not be created.\n");
				}
			}
		});
		menuItemPaste.setMnemonic(KeyEvent.VK_P);
		editMenu.add(menuItemPaste);
		
		// Pipeline Menu
		pipelineMenu = new JMenu("Pipeline");
		pipelineMenu.setMnemonic('P');
		menuBar.add(pipelineMenu);
		
		menuItemExecute = new JMenuItem("Execute");
		menuItemExecute.setEnabled(false);
		menuItemExecute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(inputPath.getText().length()==0&&inputTextArea.getText().length()==0) {
            		outputTextArea.append("No input provided.\n");
            		return;
            	}
            	if(inputPath.getText().length()==0&&inputTextArea.getText().length()>0) {
            		try {
    					String fileName = "temporaryInput.txt";
    					File file = new File(fileName);
    					PrintWriter writer = new PrintWriter(file, "UTF-8");
    					writer.println(inputTextArea.getText());
    					writer.close();
    					inputPath.setText(file.getAbsolutePath());
    				}
    				catch(IOException exception) {
    					outputTextArea.append("Temporary input file could not be created.\n");
    				}
            	}
            	if(outputPath.getText().length()==0) {
            		String fileName = inputPath.getText();
    				File file = new File(fileName);
    				if(file.isDirectory()) 
        				outputPath.setText(file.getAbsolutePath()+"\\Output");
    				else {
    					file = new File("temporaryOutput.txt");
    					outputPath.setText(file.getAbsolutePath());
    				}
            	}
                String xml;
                try {
                    xml = generateXML();
                    UUID uuid = UUID.randomUUID();
                    String configFileName = String.format(DEFAULT_SAVE_NAME, uuid.toString());
                    PrintWriter writer = new PrintWriter(configFileName, "UTF-8");
                    writer.println(xml);
                    writer.close();
                    File file = new File(configFileName);
                    configFilePath = file.getAbsolutePath();
                    menuItemShowCurrentConfigurationFile.setEnabled(true);
                    Main.execute(configFileName);
                    file.delete();
                    outputTextArea.append("Ouput Generated. \n");
                    outputShow.setEnabled(true);
                }
                catch(UnsupportedEncodingException exception) {
                	outputTextArea.append("Input file encoding not supported.\n");
                }
                catch(Exception exception) {
                	outputTextArea.append("Unknown error during processing.\n");
                }
            }
        });
		menuItemExecute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		menuItemExecute.setMnemonic(KeyEvent.VK_E);
		pipelineMenu.add(menuItemExecute);
		
		menuItemShowCurrentConfigurationFile = new JMenuItem("Show Current Configuration File");
		menuItemShowCurrentConfigurationFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(configFilePath));
					menuItemShowCurrentConfigurationFile.setEnabled(false);
				}
				catch(IOException exception) {
					outputTextArea.append("Configuration file path unreachable.\n");
				}
			}
		});
		menuItemShowCurrentConfigurationFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		menuItemShowCurrentConfigurationFile.setEnabled(false);
		menuItemShowCurrentConfigurationFile.setMnemonic(KeyEvent.VK_F);
		pipelineMenu.add(menuItemShowCurrentConfigurationFile);
		
		separatorPipelineMenu1 = new JSeparator();
		pipelineMenu.add(separatorPipelineMenu1);
		
		menuItemAddComponent = new JMenuItem("Add Component");
		menuItemAddComponent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                ComponentPanel panel = new ComponentPanel(contentFrame);
                int choice = JOptionPane.showOptionDialog(contentFrame, panel, "Add Component", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (choice == JOptionPane.OK_OPTION) {
                    Component component = panel.getComponent();
                    componentListModel.addElement(component);
                }
                if(pipelineList.getModel().getSize()!=0) {
                	pipelineExecute.setEnabled(true);
                	menuItemExecute.setEnabled(true);
                	pipelineSave.setEnabled(true);
                	menuItemSavePipeline.setEnabled(true);
                	pipelineStatusTextPane.setText("Pipeline Ready");
                }
                else {
                	pipelineStatusTextPane.setText("Pipeline Not Ready");
                }
			}
		});
		menuItemAddComponent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
		menuItemAddComponent.setMnemonic(KeyEvent.VK_A);
		pipelineMenu.add(menuItemAddComponent);
		
		menuItemRemoveComponent = new JMenuItem("Remove Component");
		menuItemRemoveComponent.setEnabled(false);
		menuItemRemoveComponent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                while (pipelineList.getSelectedIndex() != -1) {
                    componentListModel.remove(pipelineList.getSelectedIndex());
                }
                if(pipelineList.getModel().getSize()==0) {
                	pipelineExecute.setEnabled(false);
                	menuItemExecute.setEnabled(false);
                	pipelineSave.setEnabled(false);
                	menuItemSavePipeline.setEnabled(false);
                	pipelineStatusTextPane.setText("Pipeline Not Ready");
                }
                else {
                	pipelineStatusTextPane.setText("Pipeline Ready");
                }
            }
        });
		menuItemRemoveComponent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		menuItemRemoveComponent.setMnemonic(KeyEvent.VK_R);
		pipelineMenu.add(menuItemRemoveComponent);
		
		menuItemConfigureComponent = new JMenuItem("Configure Component");
		menuItemConfigureComponent.setEnabled(false);
		menuItemConfigureComponent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = pipelineList.getSelectedIndex();
				Component component = (Component) componentListModel.getElementAt(index);
                ComponentPanel panel = new ComponentPanel(contentFrame);
                panel.setComponent(component);
                int choice = JOptionPane.showOptionDialog(contentFrame, panel, "Configure Component", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (choice == JOptionPane.OK_OPTION) {
                    component = panel.getComponent();
                    componentListModel.removeElementAt(index);
                    componentListModel.insertElementAt(component, index);
                    pipelineList.setSelectedIndex(index);
                }
                if(pipelineList.getModel().getSize()!=0) {
                	pipelineExecute.setEnabled(true);
                	menuItemExecute.setEnabled(true);
                	pipelineSave.setEnabled(true);
                	menuItemSavePipeline.setEnabled(true);
                	pipelineStatusTextPane.setText("Pipeline Ready");
                }
                else {
                	pipelineStatusTextPane.setText("Pipeline Not Ready");
                }
			}
		});
		menuItemConfigureComponent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		menuItemConfigureComponent.setMnemonic(KeyEvent.VK_C);
		pipelineMenu.add(menuItemConfigureComponent);
		
		separatorPipelineMenu2 = new JSeparator();
		pipelineMenu.add(separatorPipelineMenu2);
		
		menuItemMoveUp = new JMenuItem("Move Up");
		menuItemMoveUp.setEnabled(false);
		menuItemMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                int currentPosition = pipelineList.getSelectedIndex();
                if(currentPosition > 0) {
                	Component currentComponent = componentListModel.getElementAt(currentPosition);
                	Component previousComponent = componentListModel.getElementAt(currentPosition-1);
                	componentListModel.set(currentPosition, previousComponent);
                	componentListModel.set(currentPosition-1, currentComponent);
                	pipelineList.setSelectedIndex(currentPosition-1);
                	pipelineList.ensureIndexIsVisible(currentPosition-1);
                	if(currentPosition-1 == 0) {
                		pipelineMoveUp.setEnabled(false);
                		menuItemMoveUp.setEnabled(false);
                	}
                }
			}
		});
		menuItemMoveUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
		menuItemMoveUp.setMnemonic(KeyEvent.VK_U);
		pipelineMenu.add(menuItemMoveUp);
		
		menuItemMoveDown = new JMenuItem("Move Down");
		menuItemMoveDown.setEnabled(false);
		menuItemMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int currentPosition = pipelineList.getSelectedIndex();
                if(currentPosition < (pipelineList.getModel().getSize()-1)) {
                	Component currentComponent = componentListModel.getElementAt(currentPosition);
                	Component nextComponent = componentListModel.getElementAt(currentPosition+1);
                	componentListModel.set(currentPosition, nextComponent);
                	componentListModel.set(currentPosition+1, currentComponent);
                	pipelineList.setSelectedIndex(currentPosition+1);
                	pipelineList.ensureIndexIsVisible(currentPosition+1);
                	if(currentPosition+1 == (pipelineList.getModel().getSize()-1)) {
                		pipelineMoveDown.setEnabled(false);
                		menuItemMoveDown.setEnabled(false);
                	}
                }
			}
		});
		menuItemMoveDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuItemMoveDown.setMnemonic(KeyEvent.VK_D);
		pipelineMenu.add(menuItemMoveDown);
		
		separatorPipelineMenu3 = new JSeparator();
		pipelineMenu.add(separatorPipelineMenu3);
		
		menuItemSavePipeline = new JMenuItem("Save Pipeline");
		menuItemSavePipeline.setEnabled(false);
		menuItemSavePipeline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String xml;
                try {
                    xml = savePipeline();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnVal = fileChooser.showSaveDialog(contentFrame);
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
                    else {
                        outputTextArea.append("Save pipeline command cancelled by user.\n");
                    }
                    outputTextArea.append("Pipeline saved successfully. \n");
                }
                catch (IOException exception) {
                	outputTextArea.append("Pipeline saved failed.\n");
                }
			}
		});
		menuItemSavePipeline.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
		menuItemSavePipeline.setMnemonic(KeyEvent.VK_S);
		pipelineMenu.add(menuItemSavePipeline);
		
		menuItemLoadPipeline = new JMenuItem("Load Pipeline");
		menuItemLoadPipeline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int returnVal = fileChooser.showOpenDialog(contentFrame);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						Component components[] = loadPipeline(file);
						componentListModel.removeAllElements();
						for(int i=0; i<components.length; i++) 
		                    componentListModel.addElement(components[i]);
					}
					else {
						outputTextArea.append("Load pipeline command cancelled by user.\n");
					}
				}
				catch(IOException exception) {
					outputTextArea.append("Pipeline load failed.\n");
				}
				catch(SAXException exception) {
					outputTextArea.append("Error in selected XML file.\n");
				}
				catch(ParserConfigurationException exception) {
					outputTextArea.append("Parsing of XML file failed.\n");
				}
				if(pipelineList.getModel().getSize()!=0) {
                	pipelineExecute.setEnabled(true);
                	menuItemExecute.setEnabled(true);
                	pipelineSave.setEnabled(true);
                	menuItemSavePipeline.setEnabled(true);
                	pipelineStatusTextPane.setText("Pipeline Ready");
                }
                else {
                	pipelineStatusTextPane.setText("Pipeline Not Ready");
                }
			}
		});
		menuItemLoadPipeline.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK));
		menuItemLoadPipeline.setMnemonic(KeyEvent.VK_L);
		pipelineMenu.add(menuItemLoadPipeline);
		
		// Help Menu
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		menuBar.add(helpMenu);
		
		menuItemHelp = new JMenuItem("Help");
		menuItemHelp.setEnabled(false);
		menuItemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		menuItemHelp.setMnemonic(KeyEvent.VK_H);
		helpMenu.add(menuItemHelp);
		
		menuItemAbout = new JMenuItem("About");
		menuItemAbout.setEnabled(false);
		menuItemAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		menuItemAbout.setMnemonic(KeyEvent.VK_A);
		helpMenu.add(menuItemAbout);

		contentFrame.setVisible(true);
		
	}
	
	/**
	 * This method is used to generate the configuration file which is executed during document processing.
	 * @param Nothing
	 * @return String - XML file created for execution
	 * @throws IOException
	 */
	protected String generateXML() throws IOException {
		
        // Input Type
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<config>");
        xmlBuilder.append("<input>");
        File f = new File(inputPath.getText());
        if (f.exists() && f.isFile()) {
            xmlBuilder.append("<type>File</type>");
        }
        else if (f.exists() && f.isDirectory()) {
            xmlBuilder.append("<type>Folder</type>");
        }
        else {
            throw new IOException("Input file not found");
        }
        xmlBuilder.append("<path>");
        xmlBuilder.append(inputPath.getText());
        xmlBuilder.append("</path>");
        xmlBuilder.append("<parseType>");
        xmlBuilder.append(inputFileType.getSelectedItem());
        xmlBuilder.append("</parseType>");
        xmlBuilder.append("</input>");
        
        // Component List
        xmlBuilder.append("<components>");
        for (Object obj : componentListModel.toArray())
        {
            Component component = (Component) obj;
            xmlBuilder.append("<component>");
            xmlBuilder.append("<loadType>");
            xmlBuilder.append(component.getLoadType());
            xmlBuilder.append("</loadType>");
            xmlBuilder.append("<type>");
            xmlBuilder.append(component.getType());
            xmlBuilder.append("</type>");
            xmlBuilder.append("<path>");
            xmlBuilder.append(component.getPath());
            xmlBuilder.append("</path>");
            if (!Strings.isNullOrEmpty(component.getDependencyPath()))
            {
                xmlBuilder.append("<dependencyPath>");
                xmlBuilder.append(component.getDependencyPath());
                xmlBuilder.append("</dependencyPath>");
            }
            xmlBuilder.append("<className>");
            xmlBuilder.append(component.getClassName());
            xmlBuilder.append("</className>");
            xmlBuilder.append("</component>");
        }
        xmlBuilder.append("</components>");
        
        // Output Type
        xmlBuilder.append("<output>");
        f = new File(outputPath.getText());
        if (f.exists() && f.isDirectory()) {
            xmlBuilder.append("<type>Folder</type>");
        }
        else {
            xmlBuilder.append("<type>File</type>");
        }
        xmlBuilder.append("<path>");
        xmlBuilder.append(outputPath.getText());
        xmlBuilder.append("</path>");
        xmlBuilder.append("<generateType>");
        xmlBuilder.append(outputFileType.getSelectedItem());
        xmlBuilder.append("</generateType>");
        xmlBuilder.append("</output>");
        xmlBuilder.append("</config>");
        
        return xmlBuilder.toString();
        
    }
	
	/**
	 * This method is used during Save Pipeline option to create the XML file containing pipeline data to be saved.
	 * @param Nothing
	 * @return String - XML file created for saving pipeline
	 * @throws IOException
	 */
	protected String savePipeline() throws IOException {
		StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<components>");
        for (Object obj : componentListModel.toArray())
        {
            Component component = (Component) obj;
            xmlBuilder.append("<component>");
            xmlBuilder.append("<loadType>");
            xmlBuilder.append(component.getLoadType());
            xmlBuilder.append("</loadType>");
            xmlBuilder.append("<type>");
            xmlBuilder.append(component.getType());
            xmlBuilder.append("</type>");
            xmlBuilder.append("<path>");
            xmlBuilder.append(component.getPath());
            xmlBuilder.append("</path>");
            if (!Strings.isNullOrEmpty(component.getDependencyPath()))
            {
                xmlBuilder.append("<dependencyPath>");
                xmlBuilder.append(component.getDependencyPath());
                xmlBuilder.append("</dependencyPath>");
            }
            xmlBuilder.append("<className>");
            xmlBuilder.append(component.getClassName());
            xmlBuilder.append("</className>");
            xmlBuilder.append("</component>");
        }
        xmlBuilder.append("</components>");
        return xmlBuilder.toString();
    }

	/**
	 * This method is used during Load Pipeline option to read the XML file containing details of a saved pipeline.
	 * @param {File} file - XML file containing saved details of a pipeline
	 * @return Component[] - Array of components that are loaded from the XML file
	 * @throws IOException, SAXException, ParserConfigurationException
	 */
	protected Component[] loadPipeline(File file) throws IOException, SAXException, ParserConfigurationException  {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);
        doc.getDocumentElement().normalize();
        NodeList componentList = doc.getElementsByTagName("component");
        int totalComponents = componentList.getLength();
        Component components[] = new Component[totalComponents];
        Node currentComponent;
        Element currentElement, currentSubElement;
        NodeList currentList;
        for(int i=0; i<totalComponents; i++) {
        	currentComponent = componentList.item(i);
        	components[i] = new Component();
        	if(currentComponent.getNodeType() == Node.ELEMENT_NODE) {
        		currentElement = (Element) currentComponent;
        		currentList = currentElement.getElementsByTagName("loadType");
        		if(currentList.getLength()!=0) {
                	currentSubElement = (Element)currentList.item(0);
                	currentList = currentSubElement.getChildNodes();
                	if(((Node)currentList.item(0)).getNodeValue().trim().equals("CLASS")) 
                		components[i].setLoadType(ComponentLoadType.CLASS);
                	else if(((Node)currentList.item(0)).getNodeValue().trim().equals("JAR"))
                		components[i].setLoadType(ComponentLoadType.JAR);
        		}
        		currentList = currentElement.getElementsByTagName("type");
        		if(currentList.getLength()!=0) {
                	currentSubElement = (Element)currentList.item(0);
                	currentList = currentSubElement.getChildNodes();
                	if(((Node)currentList.item(0)).getNodeValue().trim().equals("CONCEPT_EXTRACTOR")) 
                		components[i].setType(ComponentType.CONCEPT_EXTRACTOR);
                	else if(((Node)currentList.item(0)).getNodeValue().trim().equals("DE_IDENTIFIER"))
                		components[i].setType(ComponentType.DE_IDENTIFIER);
                	if(((Node)currentList.item(0)).getNodeValue().trim().equals("DICTIONARY_BUILDER")) 
                		components[i].setType(ComponentType.DICTIONARY_BUILDER);
                	else if(((Node)currentList.item(0)).getNodeValue().trim().equals("PRE_PROCESSOR"))
                		components[i].setType(ComponentType.PRE_PROCESSOR);
                	if(((Node)currentList.item(0)).getNodeValue().trim().equals("STEMMER")) 
                		components[i].setType(ComponentType.STEMMER);
                	else if(((Node)currentList.item(0)).getNodeValue().trim().equals("STOP_WORD_REMOVER"))
                		components[i].setType(ComponentType.STOP_WORD_REMOVER);
                	if(((Node)currentList.item(0)).getNodeValue().trim().equals("TOKENIZER")) 
                		components[i].setType(ComponentType.TOKENIZER);
        		}
        		currentList = currentElement.getElementsByTagName("path");
        		if(currentList.getLength()!=0) {
        			currentSubElement = (Element)currentList.item(0);
        			currentList = currentSubElement.getChildNodes();
        			components[i].setPath(((Node)currentList.item(0)).getNodeValue().trim());
        		}
        		currentList = currentElement.getElementsByTagName("dependencyPath");
        		if(currentList.getLength()!=0) {
        			currentSubElement = (Element)currentList.item(0);
                	currentList = currentSubElement.getChildNodes();
                	components[i].setDependencyPath(((Node)currentList.item(0)).getNodeValue().trim());
        		}
        		currentList = currentElement.getElementsByTagName("className");
        		if(currentList.getLength()!=0) {
                	currentSubElement = (Element)currentList.item(0);
                	currentList = currentSubElement.getChildNodes();
                	components[i].setClassName(((Node)currentList.item(0)).getNodeValue().trim());
        		}
        	}
        }
        return components;
    }
	
	/**
	 * This is the main method used to call the default constructor on a separate thread, and execute the GUI.
	 * @param {String[]} args - Command line arguments
	 * @return Nothing
	 * @exception Nothing
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainWindow();
			}
		});

	}

}