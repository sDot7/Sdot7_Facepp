/**
 * @Description TODO
 * @Author Wang Qi
 * @Date May 11, 2015
 */
package com.seandot7.facepp;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

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
	private JLabel label;
	private JFileChooser chooser;
	private JTextArea textArea;
	private String currentFilePath = null;
	private JButton createPersonButton;
	private JButton deletePersonButton;
	private JButton getPersonInfoButton;
	private JButton addFaceToPersonButton;
	private ArrayList<JButton> buttons;
	private static String trainingSessionId = null;
	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 300;
	private static final String GROUP_NAME = "test_group";

	public ImageViewerFrame() {
		super();
		setTitle("Face Recognition Demo");
		setSize(this.DEFAULT_WIDTH,this.DEFAULT_HEIGHT);
		setLayout(new FlowLayout());
		
		label = new JLabel();
		add(label);
		
		textArea = new JTextArea("information", 1, 20);
		add(textArea);
		textArea.setDisabledTextColor(Color.BLACK);
		textArea.setEnabled(false);
		textArea.setLineWrap(true);
		
		this.chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("menu");
		menuBar.add(menu);
		
		final JMenuItem openItem = new JMenuItem("open image file");
		menu.add(openItem);
		
		openItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int result = chooser.showOpenDialog(null);
				if (result==JFileChooser.APPROVE_OPTION) {
					String name = chooser.getSelectedFile().getPath();
					currentFilePath = name;
					ImageIcon tempIcon = new ImageIcon(name);
					label.setVisible(true);
					label.setIcon(tempIcon);
					setSize(Math.max(tempIcon.getIconWidth(), textArea.getWidth()) + 20, tempIcon.getIconHeight() + textArea.getHeight() + 80);
				}
				
			}
			
			
		});
		
		JMenuItem recognizeItem = new JMenuItem("recognize");
		menu.add(recognizeItem);
		
		recognizeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openItem.doClick();
				if (currentFilePath != null) {
					FaceppUtils.Pair result = FaceppUtils.recoginizeFace2(currentFilePath, GROUP_NAME);
					
					if (result != null) {
						textArea.setText("name: " + result.getFirst() + "\nconfidence: " + result.getSecond());
					} else {
						textArea.setText("cannot recognize this person");
					}
					
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
					textArea.setText("image landmarked");
				} else {
					textArea.setText("import an image file");
				}
			}
		});

		
		createPersonButton = new JButton("confirm");
		add(createPersonButton);
		createPersonButton.setVisible(false);
		createPersonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputString = textArea.getText().trim();
				String[] tempStrings = inputString.split(":");
				if (tempStrings.length != 2) {
					textArea.setText("invalid name\nname: ");
				} else {
					String name = tempStrings[1].trim();
					FaceppUtils.createPerson(name);
					FaceppUtils.addPersonToGroup(name, GROUP_NAME);
					textArea.setText("success - create person " + name);
					textArea.setEnabled(false);
					createPersonButton.setVisible(false);
				}
				
			}
		});
		JMenuItem createPersonItem = new JMenuItem("create person");
		menu.add(createPersonItem);
		createPersonItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setButtonsInvisible();
				label.setVisible(false);
				textArea.setEnabled(true);
				textArea.setText("name: ");
				createPersonButton.setVisible(true);
			}
		});

		deletePersonButton = new JButton("confirm");
		add(deletePersonButton);
		deletePersonButton.setVisible(false);
		deletePersonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputString = textArea.getText().trim();
				String[] tempStrings = inputString.split(":");
				if (tempStrings.length != 2) {
					textArea.setText("invalid name\nname: ");
				} else {
					String name = tempStrings[1].trim();
					if (FaceppUtils.deletePerson2(name)) {
						textArea.setText("success - delete person " + name);
						textArea.setEnabled(false);
						deletePersonButton.setVisible(false);
					} else {
						textArea.setText("failed - try again\n" + name);
					}
					
				}
				
			}
		});
		JMenuItem deletePersonItem = new JMenuItem("delete person");
		menu.add(deletePersonItem);
		deletePersonItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setButtonsInvisible();
				label.setVisible(false);
				textArea.setEnabled(true);
				textArea.setText("name: ");
				deletePersonButton.setVisible(true);
			}
		});
		

		getPersonInfoButton = new JButton("confirm");
		add(getPersonInfoButton);
		getPersonInfoButton.setVisible(false);
		getPersonInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPersonButton.setVisible(false);
				String inputString = textArea.getText().trim();
				String[] tempStrings = inputString.split(":");
				if (tempStrings.length != 2) {
					textArea.setText("invalid name\nname: ");
				} else {
					String name = tempStrings[1].trim();
					
					Object resultObject = FaceppUtils.getPersonInfo(name);
					if (resultObject != null)
						textArea.setText(resultObject.toString());
					else {
						textArea.setText("failed - no info or invalid name\nname: ");
					}
					
				}
				
			}
		});
		JMenuItem getPersonInfoItem = new JMenuItem("get person info");
		menu.add(getPersonInfoItem);
		getPersonInfoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setButtonsInvisible();
				label.setVisible(false);
				textArea.setEnabled(true);
				textArea.setText("name: ");
				getPersonInfoButton.setVisible(true);
			}
		});
		
		addFaceToPersonButton = new JButton("confirm");
		add(addFaceToPersonButton);
		addFaceToPersonButton.setVisible(false);
		addFaceToPersonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputString = textArea.getText().trim();
				String[] tempStrings = inputString.split(":");
				if (tempStrings.length != 2) {
					textArea.setText("invalid name\nname: ");
				} else {
					String name = tempStrings[1].trim();
					if (currentFilePath != null && FaceppUtils.addFaceToPerson2(currentFilePath, name)) {
						textArea.setText("success - add face to person " + name);
						textArea.setEnabled(false);
						addFaceToPersonButton.setVisible(false);
					} else {
						textArea.setText("failed - try again\nname: " + name);
					}
				}
				
			}
		});
		JMenuItem addFaceToPersonItem = new JMenuItem("add face to person");
		menu.add(addFaceToPersonItem);
		addFaceToPersonItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setButtonsInvisible();
				label.setVisible(true);
				openItem.doClick();
				textArea.setEnabled(true);
				textArea.setText("name: ");
				addFaceToPersonButton.setVisible(true);
			}
		});
	
		
		JMenuItem getPersonListItem = new JMenuItem("get person list");
		menu.add(getPersonListItem);
		getPersonListItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setButtonsInvisible();
				label.setVisible(false);
				textArea.setEnabled(false);
				ArrayList<String> personList = FaceppUtils.getPersonList2();
				textArea.setText("person list - " + personList);
				
			}
		});
		
		JMenuItem trainItem = new JMenuItem("train");
		menu.add(trainItem);
		trainItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setButtonsInvisible();
				label.setVisible(false);
				textArea.setEnabled(false);
				textArea.setText("invoke training");
				trainingSessionId = FaceppUtils.trainIdentify(GROUP_NAME);
			}
		});
		
		JMenuItem getTraningStatusItem = new JMenuItem("get training status");
		menu.add(getTraningStatusItem);
		getTraningStatusItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (trainingSessionId != null) {
					setButtonsInvisible();
					label.setVisible(false);
					textArea.setEnabled(false);
					String status = FaceppUtils.getTraninigStatus(trainingSessionId);
					textArea.setText("training status - " + status);
				}
				
			}
		});
		
		
		JMenuItem exitItem = new JMenuItem("quit");
		menu.add(exitItem);
		exitItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
			    System.exit(0);
			}
			
		});
		
		buttons = new ArrayList<JButton>();
		buttons.add(createPersonButton);
		buttons.add(addFaceToPersonButton);
		buttons.add(deletePersonButton);
		buttons.add(getPersonInfoButton);
		
	}
	
	void setButtonsInvisible() {
		for (JButton button : buttons) {
			button.setVisible(false);
		}
	}

}
