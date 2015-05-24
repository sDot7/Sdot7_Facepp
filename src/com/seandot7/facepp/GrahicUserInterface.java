/**
 * @Description TODO
 * @Author Wang Qi
 * @Date May 11, 2015
 */
package com.seandot7.facepp;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.json.JSONException;
import org.json.JSONObject;

import com.seandot7.facepp.FaceppUtils.Pair;

public class GrahicUserInterface {

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new ImageViewerFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}



class ImageViewerFrame extends JFrame {
	public static final int DEFAULT_POSITION_X = 200;
	public static final int DEFAULT_POSITION_Y = 100;
	
	
	private JLabel label;
	private JFileChooser chooser;
	private String currentFilePath = null;
	private String currentPersonName = "";
	private String currentGroupName = "test_group";
	private ArrayList<JButton> buttons;
	private static String trainingSessionId = null;
	private static final String DEFAULT_GROUP_NAME = "test_group";
	private int scaledWidth = 600;
	private int scaledHeight = 400;
	private JTextArea consoleJTextArea;
	private JMenuBar menuBar;
	
	// user info window
	private JFrame userInfoJFrame;
	private JButton confirmJButton;
	private JButton editJButton;
	private JLabel userNameJLabel;
	private JLabel groupNameJLabel;
	private JTextArea userNameJTextArea;
	private JTextArea groupNameJTextArea;
	private int initWidth = 620;
	private int initHeight = 420;
	
	private int userInfoFrameWidth;
	private int userInfoFrameHeight;
	
	public ImageViewerFrame() {
		super();
		setTitle("Face Recognition Demo");
		setLayout(null);
//		setLayout(new FlowLayout());
		
		initUserInfoWindow();
		final int consoleTextHeight = 16;
		consoleJTextArea = new JTextArea("TEST", 8, 26);
		consoleJTextArea.setSize(scaledWidth-10, consoleTextHeight * (consoleJTextArea.getRows() + 1));
		consoleJTextArea.setBounds(0, userInfoFrameHeight + 5, consoleJTextArea.getWidth(), consoleJTextArea.getHeight());
		consoleJTextArea.setEnabled(false);
		consoleJTextArea.setDisabledTextColor(Color.BLACK);
		consoleJTextArea.setFont(new Font("Serif", 0, 14)); 
		consoleJTextArea.setLineWrap(true);
		
	    JPanel panelOutput;

	    panelOutput = new JPanel();
	    panelOutput.setLayout(new GridLayout(1, 1));
	    panelOutput.add(new JScrollPane(consoleJTextArea));
	    panelOutput.setSize(scaledWidth-10, consoleTextHeight * consoleJTextArea.getRows());
	    panelOutput.setBounds(0, userInfoFrameHeight + 5, consoleJTextArea.getWidth(), consoleJTextArea.getHeight());
//		panelOutput.setVisible(true);
		add(panelOutput);
//		add(consoleJTextArea);
		
		label = new JLabel();
		label.setSize(scaledWidth, scaledHeight);
		label.setBounds(0, consoleJTextArea.getY() + consoleJTextArea.getHeight() + 5, scaledWidth, scaledHeight);
		label.setVisible(true);
		add(label);
		
//		textArea = new JTextArea("information", 1, 20);
//		add(textArea);
//		textArea.setDisabledTextColor(Color.BLACK);
//		textArea.setEnabled(false);
//		textArea.setLineWrap(true);
		
		this.chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("menu");
		menuBar.add(menu);
		
		final JMenuItem openItem = new JMenuItem("open image file");
		menu.add(openItem);
		
		openItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chooser.setMultiSelectionEnabled(false);
				int result = chooser.showOpenDialog(null);
				if (result==JFileChooser.APPROVE_OPTION) {
					String name = chooser.getSelectedFile().getPath();
					currentFilePath = name;
					ImageIcon tempIcon = new ImageIcon(name);
					Image tempImage = tempIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
					ImageIcon tempIcon2 = new ImageIcon(tempImage);
					label.setVisible(true);
					label.setIcon(tempIcon2);
					// TODO
					repaint();
				}
				
			}
		});
		
		JMenuItem recognizeItem = new JMenuItem("recognize");
		menu.add(recognizeItem);
		
		recognizeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					openItem.doClick();
					if (currentFilePath != null) {
						try {
							List<Pair> candidates = FaceppUtils.recoginizeFace2(currentFilePath, currentGroupName);
							String tempString = "Candidates";
							if (candidates == null)
								throw new Exception("no candidates");
							
							for (int i=0; i<2; ++i) {
								if (i >= candidates.size())
									break;
								tempString += "[" + candidates.get(i).getFirst() + ", " + candidates.get(i).getSecond() + "]";
							}
							
							consoleJTextArea.setText(tempString);
							repaint();
						} catch(Exception e1) {
							e1.printStackTrace();
							consoleJTextArea.setText("no cnadidates");
							repaint();
						}
						
					}
					
				} else {
					consoleJTextArea.setText("please confirm your group info");
					repaint();
				}
				
			}
		});
		
		JMenuItem landmarkItem = new JMenuItem("landmark");
		menu.add(landmarkItem);
		
		landmarkItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentFilePath != null) {
					FaceppUtils.display(currentFilePath);
					consoleJTextArea.setText("image landmarked");
					repaint();
				} else {
					consoleJTextArea.setText("import an image file");
					repaint();
				}
			}
		});

		JMenuItem trainItem = new JMenuItem("train");
		menu.add(trainItem);
		trainItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					try {
						trainingSessionId = FaceppUtils.trainIdentify(currentGroupName);
						consoleJTextArea.setText("success invoke training");
					} catch(Exception e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to train");
					}
				} else {
					consoleJTextArea.setText("please confirm your group info");
				}
				repaint();
				
			}
		});
		
		JMenuItem getTraningStatusItem = new JMenuItem("get training status");
		menu.add(getTraningStatusItem);
		getTraningStatusItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (trainingSessionId != null) {
					try {
						String status = FaceppUtils.getTraninigStatus(trainingSessionId);
						consoleJTextArea.setText(status);
					} catch(Exception e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to train");
					}
				}
				
			}
		});
		
		JMenuItem fakePersonInfoItem = new JMenuItem("====== person ======");
		fakePersonInfoItem.setEnabled(false);
		menu.add(fakePersonInfoItem);
		
		JMenuItem createPersonItem = new JMenuItem("create person");
		menu.add(createPersonItem);
		createPersonItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					try {
						JSONObject result = FaceppUtils.createPerson(currentPersonName);
						if (result != null) {
							consoleJTextArea.setText("success create person");
						} else {
							consoleJTextArea.setText("failed to create new person, pls try again");
						}
					} catch(Exception e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to create new person, pls try again");
					}
				} else {
					consoleJTextArea.setText("please confirm your user name");
				}
				repaint();
			}
		});

	
		JMenuItem deletePersonItem = new JMenuItem("delete person");
		menu.add(deletePersonItem);
		deletePersonItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					try {
						JSONObject result = FaceppUtils.deletePerson(currentPersonName);
						if (result.getBoolean("success")) {
							consoleJTextArea.setText("success delete person");
						} else {
							consoleJTextArea.setText("failed to delete person, pls try again");
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to delete person, pls try again");
					}
				} else {
					consoleJTextArea.setText("please confirm your user name");
				}
				repaint();
			}
		});
		
		JMenuItem getPersonInfoItem = new JMenuItem("get person info");
		menu.add(getPersonInfoItem);
		getPersonInfoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					try {
						JSONObject result = FaceppUtils.getPersonInfo(currentPersonName);
						if (result != null) {
							consoleJTextArea.setText(result.toString(2));
						} else {
							consoleJTextArea.setText("failed to get person info, pls try again");
						}
					} catch(Exception e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to get person info, pls try again");
					}
				} else {
					consoleJTextArea.setText("please confirm your user name");
				}
				repaint();
			}
		});
		
		JMenuItem addPersonToGroup = new JMenuItem("add a person to a group");
		menu.add(addPersonToGroup);
		addPersonToGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					try {
						boolean result = FaceppUtils.addPersonToGroup2(currentPersonName, currentGroupName);
						if (result) {
							consoleJTextArea.setText("success add the person to the group");
						} else {
							consoleJTextArea.setText("failed to add the person to the group, pls try again");
						}
					} catch(Exception e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to add the person to the group, pls try again");
					}
				} else {
					consoleJTextArea.setText("please confirm your user name & group");
				}
				repaint();
			}
		});
		
		JMenuItem addAFaceToPersonItem = new JMenuItem("add a face to person");
		menu.add(addAFaceToPersonItem);
		addAFaceToPersonItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					openItem.doClick();
					try {
						boolean result = FaceppUtils.addFaceToPerson2(currentFilePath, currentPersonName);
						if (result) {
							consoleJTextArea.setText("success add a face to the user");
						} else {
							consoleJTextArea.setText("failed to add a face to the user, pls try again");
						}
					} catch(Exception e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to add a face to the user, pls try again");
					}
				} else {
					consoleJTextArea.setText("please confirm your user name");
				}
				repaint();
			}
		});
		
		// TODO
		JMenuItem addFacesToPersonItem = new JMenuItem("add faces to person");
		menu.add(addFacesToPersonItem);
		addFacesToPersonItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					chooser.setMultiSelectionEnabled(true);
					int result = chooser.showOpenDialog(null);
					if (result==JFileChooser.APPROVE_OPTION) {
						ArrayList<String> filePaths = new ArrayList<String>();
						File[] files = chooser.getSelectedFiles();
						for (File file : files) {
							String name = file.getPath();
							filePaths.add(name);
						}
						// TODO
						
						try {
							boolean result2 = FaceppUtils.addFacesToPerson(filePaths, currentPersonName);
							if (result2) {
								consoleJTextArea.setText("success add faces to the user");
							} else {
								consoleJTextArea.setText("failed to add faces to the user, pls try again");
							}
						} catch(Exception e1) {
							e1.printStackTrace();
							consoleJTextArea.setText("failed to add faces to the user, pls try again");
						}
					}
				} else {
					consoleJTextArea.setText("please confirm your user name");
				}
				repaint();
			}
		});
	
		
		JMenuItem getPersonListItem = new JMenuItem("get person list");
		menu.add(getPersonListItem);
		getPersonListItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					List<String> result = FaceppUtils.getPersonList2();
					if (result != null) {
						consoleJTextArea.setText(result.toString());
					} else {
						consoleJTextArea.setText("failed to get person list, pls try again");
					}
				} catch(Exception e1) {
					e1.printStackTrace();
					consoleJTextArea.setText("failed to get person list, pls try again");
				}
				repaint();
				
			}
		});
		


		JMenuItem fakeGroupInfoItem = new JMenuItem("====== group ======");
		fakeGroupInfoItem.setEnabled(false);
		menu.add(fakeGroupInfoItem);
		
		
		JMenuItem createtGroupItem = new JMenuItem("create group");
		menu.add(createtGroupItem);
		createtGroupItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					try {
						JSONObject result = FaceppUtils.createGroup(currentGroupName);
						if (result != null) {
							consoleJTextArea.setText("success create group");
						} else {
							consoleJTextArea.setText("failed to create new group, pls try again");
						}
					} catch(Exception e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to create new group, pls try again");
					}
				} else {
					consoleJTextArea.setText("please confirm your group");
				}
				repaint();
			}
		});
		
		
		JMenuItem deleteGropuItem = new JMenuItem("delete group");
		menu.add(deleteGropuItem);
		deleteGropuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					try {
						JSONObject result = FaceppUtils.deleteGroup(currentGroupName);
						if (result.getBoolean("success")) {
							consoleJTextArea.setText("success delete group");
						} else {
							consoleJTextArea.setText("failed to delete group, pls try again");
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to delete group, pls try again");
					}
				} else {
					consoleJTextArea.setText("please confirm your group");
				}
				repaint();
			}
		});
		
		
		JMenuItem getGroupInfoItem = new JMenuItem("get group info");
		menu.add(getGroupInfoItem);
		getGroupInfoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userInfoJFrame.setVisible(true);
				if (!confirmJButton.isEnabled()) {
					try {
						JSONObject result = FaceppUtils.getGroupInfo(currentGroupName);
						if (result != null) {
							consoleJTextArea.setText(result.toString(2));
						} else {
							consoleJTextArea.setText("failed to get group info, pls try again");
						}
					} catch(Exception e1) {
						e1.printStackTrace();
						consoleJTextArea.setText("failed to get group info, pls try again");
					}
				} else {
					consoleJTextArea.setText("please confirm your group");
				}
				repaint();
			}
		});
		
		

		JMenuItem getGroupListItem = new JMenuItem("get group list");
		menu.add(getGroupListItem);
		getGroupListItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					List<String> result = FaceppUtils.getGroupList2();
					if (result != null) {
						consoleJTextArea.setText(result.toString());
					} else {
						consoleJTextArea.setText("failed to get group list, pls try again");
					}
				} catch(Exception e1) {
					e1.printStackTrace();
					consoleJTextArea.setText("failed to get group list, pls try again");
				}
				repaint();
				
			}
		});
		
		JMenuItem fakeItem = new JMenuItem("");
		fakeItem.setEnabled(false);
		menu.add(fakeItem);
		
		JMenuItem exitItem = new JMenuItem("quit");
		menu.add(exitItem);
		exitItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
			    System.exit(0);
			}
			
		});
		
		initWidth = scaledWidth + 10;
		initHeight = menuBar.getHeight() + scaledHeight + consoleJTextArea.getHeight() + 100 + userInfoFrameHeight;
		setBounds(DEFAULT_POSITION_X, DEFAULT_POSITION_Y, initWidth, initHeight);
		setSize(initWidth, initHeight);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		// init user info window
		
		
	}
	
	// init sub window user info
	private void initUserInfoWindow() {
		
		userInfoFrameWidth = initWidth;
		userInfoFrameHeight = 40;
//		userInfoJFrame = new JFrame();
		userInfoJFrame = this;
//		userInfoJFrame.setLayout(null);
//		userInfoJFrame.setTitle("user info");
//		userInfoJFrame.setSize(userInfoFrameWidth, userInfoFrameHeight);
//		userInfoJFrame.setLayout(null);
//		userInfoJFrame.setBounds(x, y, userInfoFrameWidth, userInfoFrameHeight + 20);
		
		
		int userInfoWidth = userInfoFrameWidth / 6;
		int userInfoHeight = userInfoFrameHeight - 15;

		userNameJLabel = new JLabel();
		userNameJLabel.setText("user name:");
		userNameJLabel.setSize(userInfoWidth, userInfoHeight);
		userNameJLabel.setBounds(0, 0, userInfoWidth, userInfoHeight);
//		userNameJLabel.setFont(new Font("Serif", 0, 20));
		userNameJLabel.setVisible(true);
		userInfoJFrame.add(userNameJLabel);
		
		userNameJTextArea = new JTextArea();
		userNameJTextArea.setSize(userInfoWidth, userInfoHeight);
		userNameJTextArea.setBounds(1 * userInfoWidth, 0, userInfoWidth, userInfoHeight);
		userNameJTextArea.setFont(new Font("Serif", 0, 20));
		userNameJTextArea.setText(" ");
		userNameJTextArea.setVisible(true);
		userInfoJFrame.add(userNameJTextArea);
		
		groupNameJLabel = new JLabel();
		groupNameJLabel.setText("group name:");
		groupNameJLabel.setSize(userInfoWidth, userInfoHeight);
		groupNameJLabel.setBounds(2 * userInfoWidth, 0, userInfoWidth, userInfoHeight);
//		groupNameJLabel.setFont(new Font("Serif", 0, 20));
		groupNameJLabel.setVisible(true);
		userInfoJFrame.add(groupNameJLabel);
		
		groupNameJTextArea = new JTextArea();
		groupNameJTextArea.setSize(userInfoWidth, userInfoHeight);
		groupNameJTextArea.setBounds(3 * userInfoWidth, 0, userInfoWidth, userInfoHeight);
		groupNameJTextArea.setFont(new Font("Serif", 0, 20));
		groupNameJTextArea.setText(DEFAULT_GROUP_NAME);
		groupNameJTextArea.setVisible(true);
		userInfoJFrame.add(groupNameJTextArea);
		
		// confirm button
		confirmJButton = new JButton();
		confirmJButton.setText("confirm");
		confirmJButton.setSize(userInfoWidth, userInfoHeight);
		confirmJButton.setBounds(4 * userInfoWidth, 0, userInfoWidth, userInfoHeight);	
		userInfoJFrame.add(confirmJButton);
		confirmJButton.setVisible(true);
		confirmJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					confirmJButton.setEnabled(false);
					editJButton.setEnabled(true);
					userNameJTextArea.setEnabled(false);
					groupNameJTextArea.setEnabled(false);
					currentPersonName = userNameJTextArea.getText().trim();
					currentGroupName = groupNameJTextArea.getText().trim();
					
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
	
		// edit button
		editJButton = new JButton();
		editJButton.setText("edit");
		editJButton.setSize(userInfoWidth, userInfoHeight);
		editJButton.setBounds(5 * userInfoWidth, 0, userInfoWidth, userInfoHeight);	
		userInfoJFrame.add(editJButton);
		editJButton.setVisible(true);
		editJButton.setEnabled(false);
		editJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					confirmJButton.setEnabled(true);
					editJButton.setEnabled(false);
					userNameJTextArea.setEnabled(true);
					groupNameJTextArea.setEnabled(true);
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		userInfoJFrame.setVisible(true);
	}
	
	void setButtonsInvisible() {
		for (JButton button : buttons) {
			button.setVisible(false);
		}
	}

}
