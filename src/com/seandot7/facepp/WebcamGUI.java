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
import java.awt.image.BufferedImage;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

public class WebcamGUI {

	// GUI
	private JFrame window;
	
	private JLabel cameraFrameJLabel;
	private static final String DEFAULT_GROUP_NAME = "test_group";
	private JTextArea consoleJTextArea;
	private int scaledWidth = 600;
	private int scaledHeight = 400;
	final private CameraCapture cameraCapture = new CameraCapture();
	private JButton confirmJButton;
	private JButton editJButton;
	private JTextArea userNameJTextArea;
	private JTextArea groupNameJTextArea;
	
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
		
		this.window = new JFrame();
		this.window.setTitle("Face Recognition");
		this.window.setLayout(null);
		this.window.setBackground(Color.WHITE);
		
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
		this.window.add(this.cameraFrameJLabel);
	
		
		JMenu menu = new JMenu("menu");
		menuBar.add(menu);
		
		final JMenuItem recognizeItem = new JMenuItem("recognize");
		menu.add(recognizeItem);
		
		recognizeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				consoleJTextArea.setText("recognize");
				window.repaint();
			}
		});
		
		final JMenuItem recordItem = new JMenuItem("record");
		menu.add(recordItem);
		
		recordItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				consoleJTextArea.setText("record");
				window.repaint();
			}
		});
	
		
		int userInfoHeight = 50;
		userNameJTextArea = new JTextArea();

		
		// confirm button
		confirmJButton = new JButton();
		confirmJButton.setText("confirm");
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
		window.add(editJButton);
		editJButton.setVisible(true);
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
		consoleJTextArea.setBounds(0, this.scaledHeight, consoleJTextArea.getWidth(), consoleJTextArea.getHeight());
		consoleJTextArea.setEnabled(false);
		consoleJTextArea.setDisabledTextColor(Color.BLACK);
		consoleJTextArea.setFont(new Font("Serif", 0, 20));
		this.window.add(consoleJTextArea);
		
		this.window.setSize(this.scaledWidth, this.scaledHeight + menuBar.getHeight() + consoleTextHeight);
		// refresh window periodically
		
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				try {
					BufferedImage bufferedImage = cameraCapture.getBufferedImageOfFrame();
					if (bufferedImage != null) {
						Graphics tempGraphics = bufferedImage.getGraphics();
						tempGraphics.fillRect(10, 10, 30, 30);
						tempGraphics.dispose();
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
		
		int startTime = 2000;
		cameraCapture.getTimer().schedule(timerTask, startTime, cameraCapture.getPeriod());
		
	}

	
	public static void main(String[] args) {
		
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
