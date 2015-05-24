/**
 * @Description TODO
 * @Author Wang Qi
 * @Date May 22, 2015
 */
package com.seandot7.facepp;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

import com.seandot7.facepp.FaceppUtils.Pair;
import com.seandot7.utils.StringUtils;


public class WebcamGUI {
	private static final String DEFAULT_GROUP_NAME = "test_group";
	private static final String DEFAULT_IMAGE_FILE_PATH = "./test.jpg";
	private static final int DEFAULT_TRAINING_AMOUNT = 10;

	// GUI
	private JFrame window;
	
	private JLabel cameraFrameJLabel;
	private JTextArea consoleJTextArea;
	private int scaledWidth = 600;
	private int scaledHeight = 400;
	final private CameraCapture cameraCapture = new CameraCapture();
	private JButton confirmJButton;
	private JButton editJButton;
	private JLabel userNameJLabel;
	private JLabel groupNameJLabel;
	private JTextArea userNameJTextArea;
	private JTextArea groupNameJTextArea;
	private Timer recordTimer;
	private TimerTask recordTimerTask;
	private boolean recordFlag = false;
	private Integer recordCount;
	private Integer recordSuccessCount;
	
	
	public int getScaledWidth() {
		return scaledWidth;
	}

	public void setScaledWidth(int scaledWidth) {
		this.scaledWidth = scaledWidth;
	}

	public int getScaledHeight() {
		return scaledHeight;
	}

	public void setScaledHeight(int scaledHeight) {
		this.scaledHeight = scaledHeight;
	}

	public JFrame getWindow() {
		return this.window;
	}
	
	public void initWindow() {
		
		cameraCapture.on();
		cameraCapture.setImageFilePath(DEFAULT_IMAGE_FILE_PATH);
		
		this.window = new JFrame();
		this.window.setTitle("Face Recognition");
		this.window.setLayout(null);
		this.window.setBackground(Color.WHITE);
		this.window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
//				cameraCapture.off();
				System.exit(0);
			}
		});
		
		// set menu bar
		JMenuBar menuBar = new JMenuBar();
		int menuBarHeight = 50;
		menuBar.setSize(this.scaledWidth, menuBarHeight);
		menuBar.setBounds(0, 0, this.scaledWidth, menuBar.getHeight());
		this.window.setJMenuBar(menuBar);
		
		// set camera frame
		this.cameraFrameJLabel = new JLabel();
		this.cameraFrameJLabel.setSize(this.scaledWidth, this.scaledHeight);
		this.cameraFrameJLabel.setBounds(0, 0, this.scaledWidth, this.scaledHeight);
		cameraFrameJLabel.setVisible(true);
		this.window.add(this.cameraFrameJLabel);
	
		
		JMenu menu = new JMenu("menu");
		menuBar.add(menu);
		
		final JMenuItem recognizeItem = new JMenuItem("recognize");
		menu.add(recognizeItem);
		
		recognizeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!confirmJButton.isEnabled()) {
					// recognize the person by group name thorugh FaceppUtils
					consoleJTextArea.setText("Recognizing...");
					window.repaint();
					
					try {
//						BufferedImage bufferedImage = cameraCapture.getBufferedImageOfFrame();
//						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//						ImageIO.write(bufferedImage, "jpg", outputStream);
						byte[] imageBytes = cameraCapture.getBytesImageOfFrame();
						String currentGroupName = groupNameJTextArea.getText().trim();
						
//						// File interface
//						cameraCapture.saveAsImageFile();
//						Pair resultPair = FaceppUtils.recoginizeFace2(cameraCapture.getImageFilePath(), currentGroupName);
//						System.out.println(resultPair.getFirst() + resultPair.getSecond());
						
						
						List<Pair> candidates = FaceppUtils.recoginizeFace(imageBytes, currentGroupName);
						String tempString = "Candidates";
						if (candidates == null)
							throw new Exception("no candidates");
						
						for (int i=0; i<2; ++i) {
							if (i >= candidates.size())
								break;
							tempString += "[" + candidates.get(i).getFirst() + ", " + candidates.get(i).getSecond() + "]";
						}
						
						consoleJTextArea.setText(tempString);
						window.repaint();
						
					} catch (Exception e1) {
						consoleJTextArea.setText("Failed: cannot convert frame to byte[]");
						window.repaint();
						e1.printStackTrace();
					}
					
					
				} else {
					consoleJTextArea.setText("Please confirm the user info first");				
				}
				window.repaint();
			}
		});
		
		// record procedure
		final JMenuItem recordItem = new JMenuItem("record");
		menu.add(recordItem);
		

		recordTimerTask = new TimerTask() {
			@Override
			public void run() {
				String tempUserName = userNameJTextArea.getText().trim();
				String tempGroupName = groupNameJTextArea.getText().trim();
				if (recordCount < DEFAULT_TRAINING_AMOUNT) {
					try {
//						// bytes interface
//						byte[] imageBytes = cameraCapture.getBytesImageOfFrame();
//						if (imageBytes != null) {
//							boolean tempResult = FaceppUtils.addFaceToPerson(imageBytes, tempUserName);
//							if (tempResult) {
//								recordSuccessCount++;
//							}
//							recordCount++;
//						} else {
//							recordTimer.cancel();
//							consoleJTextArea.setText("Record failed: cannot get frame of webcam");
//							window.repaint();
//						}
						
						// file interface
						String defaultImageFilePath = cameraCapture.getImageFilePath();
						String[] tempStrings = defaultImageFilePath.split("/");
						tempStrings[tempStrings.length-1] = tempUserName + "_" + recordSuccessCount + ".jpg";
						String tempNewImageFilePath = StringUtils.join("/", tempStrings);
						cameraCapture.setImageFilePath(tempNewImageFilePath);
						cameraCapture.saveAsImageFile();
						cameraCapture.setImageFilePath(defaultImageFilePath);
//						String tempImageFilePath = cameraCapture.getImageFilePath();
						if (tempNewImageFilePath != null) {
							boolean tempResult = FaceppUtils.addFaceToPerson2(tempNewImageFilePath, tempUserName);
							if (tempResult) {
								recordSuccessCount++;
							}
							recordCount++;
						} else {
							recordTimer.cancel();
							consoleJTextArea.setText("Record failed: cannot get frame of webcam");
							window.repaint();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					recordTimer.cancel();
					recordCount = 0;
					recordFlag = false;
					if (recordSuccessCount == DEFAULT_TRAINING_AMOUNT) {
						consoleJTextArea.setText("Record success");
					} else {
						consoleJTextArea.setText("Record failed: cannot add all faces to, please try again" + userNameJTextArea.getText().trim());
						FaceppUtils.deletePerson2(tempUserName);
					}
					window.repaint();
					FaceppUtils.trainIdentify(tempGroupName);
				}

			}
		};
		
		recordItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!confirmJButton.isEnabled()) {
					// recognize the person by group name thorugh FaceppUtils
					consoleJTextArea.setText("Recording...");
					window.repaint();
					List<String> personNames = FaceppUtils.getPersonList2();
					String tempUserName = userNameJTextArea.getText().trim();
					if (!personNames.contains(tempUserName)) {
						String tempGroupName = groupNameJTextArea.getText().trim();
						FaceppUtils.createPerson(tempUserName);
						FaceppUtils.addPersonToGroup2(tempUserName, tempGroupName);
						
						recordCount = 0;
						recordSuccessCount = 0;
						recordFlag = true;
						recordTimer = new Timer();
						recordTimer.schedule(recordTimerTask, 0, cameraCapture.getPeriod() * 2);
					} else {
						consoleJTextArea.setText("This person has already recorded");
						window.repaint();
					}
					
					
					
				} else {
					consoleJTextArea.setText("Please confirm the user info(group name) first");				
				}
				window.repaint();
			}
		});
		// record end
		
		// 
		int userInfoWidth = scaledWidth / 6;
		int userInfoHeight = 28;

		userNameJLabel = new JLabel();
		userNameJLabel.setText("user name:");
		userNameJLabel.setSize(userInfoWidth, userInfoHeight);
		userNameJLabel.setBounds(0, scaledHeight, userInfoWidth, userInfoHeight);
//		userNameJLabel.setFont(new Font("Serif", 0, 20));
		userNameJLabel.setVisible(true);
		window.add(userNameJLabel);
		
		userNameJTextArea = new JTextArea();
		userNameJTextArea.setSize(userInfoWidth, userInfoHeight);
		userNameJTextArea.setBounds(1 * userInfoWidth, scaledHeight, userInfoWidth, userInfoHeight);
		userNameJTextArea.setFont(new Font("Serif", 0, 22));
		userNameJTextArea.setVisible(true);
		window.add(userNameJTextArea);
		
		groupNameJLabel = new JLabel();
		groupNameJLabel.setText("group name:");
		groupNameJLabel.setSize(userInfoWidth, userInfoHeight);
		groupNameJLabel.setBounds(2 * userInfoWidth, scaledHeight, userInfoWidth, userInfoHeight);
//		groupNameJLabel.setFont(new Font("Serif", 0, 20));
		groupNameJLabel.setVisible(true);
		window.add(groupNameJLabel);
		
		groupNameJTextArea = new JTextArea();
		groupNameJTextArea.setSize(userInfoWidth, userInfoHeight);
		groupNameJTextArea.setBounds(3 * userInfoWidth, scaledHeight, userInfoWidth, userInfoHeight);
		groupNameJTextArea.setFont(new Font("Serif", 0, 22));
		groupNameJTextArea.setText(DEFAULT_GROUP_NAME);
		groupNameJTextArea.setVisible(true);
		window.add(groupNameJTextArea);
		
		// confirm button
		confirmJButton = new JButton();
		confirmJButton.setText("confirm");
		confirmJButton.setSize(userInfoWidth, userInfoHeight);
		confirmJButton.setBounds(4 * userInfoWidth, scaledHeight, userInfoWidth, userInfoHeight);	
		window.add(confirmJButton);
		confirmJButton.setVisible(true);
		confirmJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					confirmJButton.setEnabled(false);
					editJButton.setEnabled(true);
					userNameJTextArea.setEnabled(false);
					groupNameJTextArea.setEnabled(false);
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
	
		// edit button
		editJButton = new JButton();
		editJButton.setText("edit");
		editJButton.setSize(userInfoWidth, userInfoHeight);
		editJButton.setBounds(5 * userInfoWidth, scaledHeight, userInfoWidth, userInfoHeight);	
		window.add(editJButton);
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
		
		
		final int consoleTextHeight = 20;
		consoleJTextArea = new JTextArea("TEST", 1, 80);
		consoleJTextArea.setSize(this.scaledWidth, consoleTextHeight);
		consoleJTextArea.setBounds(0, this.scaledHeight + userInfoHeight + 5, consoleJTextArea.getWidth(), consoleJTextArea.getHeight());
		consoleJTextArea.setEnabled(false);
		consoleJTextArea.setDisabledTextColor(Color.BLACK);
		consoleJTextArea.setFont(new Font("Serif", 0, 20));
		this.window.add(consoleJTextArea);
		
		this.window.setSize(this.scaledWidth, this.scaledHeight + menuBar.getHeight() + consoleTextHeight + userInfoHeight + 5);
		// refresh window periodically
		
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				try {
					BufferedImage bufferedImage = cameraCapture.getBufferedImageOfFrame();
					if (bufferedImage != null) {
						if (recordFlag) {
							Graphics tempGraphics = bufferedImage.getGraphics();
							tempGraphics.setColor(Color.RED);
							tempGraphics.fillOval(10, 10, 30, 30);
							tempGraphics.dispose();
						}
						ImageIcon icon = new ImageIcon(bufferedImage);
						icon.setImage(cameraCapture.getScaledImageOfFrame());
						cameraFrameJLabel.removeAll();
						cameraFrameJLabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight()); 
						cameraFrameJLabel.setHorizontalAlignment(0);
						cameraFrameJLabel.setIcon(icon);
						window.repaint();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}

			}
		};
		
		int startTime = 1000;
		cameraCapture.getTimer().schedule(timerTask, startTime, cameraCapture.getPeriod());
		
	}

	
	public static void main(String[] args) {
		FaceppUtils.debug = false;
		final WebcamGUI webcamGUI = new WebcamGUI();
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				webcamGUI.initWindow();
				JFrame window = webcamGUI.getWindow();
				window.setVisible(true);
			}
		});

	}

}
