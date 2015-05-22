package com.seandot7.facepp;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @Description TODO
 * @Author Wang Qi
 * @Date May 6, 2015
 */

public class ImageUtils {

	private File srcFile;
	private File dstFile;
	private BufferedImage bufferedImage;
	
	public ImageUtils(String srcFileName, String dstFileName) {
		this.srcFile = new File(srcFileName);
		this.dstFile = new File(dstFileName);
		try {
			bufferedImage = ImageIO.read(this.srcFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void display() {
		display(this.dstFile);
	}
	
	public static void display(File file) {
		if (file.isFile()) {
			JFrame frame = new JFrame();
			JLabel label = new JLabel();
			ImageIcon icon = new ImageIcon(file.getPath());  
			icon.setImage(icon.getImage().getScaledInstance(icon.getIconWidth(), icon.getIconHeight(), Image.SCALE_DEFAULT));
			label.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight()); 
			label.setHorizontalAlignment(0);
			label.setIcon(icon);
			frame.setSize(icon.getIconWidth(), icon.getIconHeight());
			frame.add(label);
			frame.setVisible(true);
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}
	
	public void drawPoint(double x, double y, double size) {
		Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
		Ellipse2D ellipse = new Ellipse2D.Double(x, y, size, size);
		graphics.draw(ellipse);
		graphics.fill(ellipse);
		graphics.dispose();
	}
	
	public void drawRectangle(double x, double y, double width, double height) {
		Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
		Rectangle2D rectantle = new Rectangle2D.Double(x, y, width, height);
		graphics.draw(rectantle);
		graphics.dispose();
	}
	
	public void write() {
		try {
			ImageIO.write(bufferedImage, "jpg", dstFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public byte[] image2byte(String path) {
//    byte[] data = null;
//    FileImageInputStream input = null;
//    try {
//		input = new FileImageInputStream(new File(path));
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		byte[] buf = new byte[1024];
//		int numBytesRead = 0;
//		while ((numBytesRead = input.read(buf)) != -1) {
//			output.write(buf, 0, numBytesRead);
//		}
//		data = output.toByteArray();
//		output.close();
//		input.close();
//    } catch (FileNotFoundException ex1) {
//    	ex1.printStackTrace();
//    } catch (IOException ex1) {
//    ex1.printStackTrace();
//    }
//    return data;
//}
	
	
	public static void main(String[] args) {
		ImageUtils context = new ImageUtils("/Users/sean_7/projects/FacePlusPlus/pics/messi_01.jpg", "/Users/sean_7/projects/FacePlusPlus/pics/test.jpg");
		context.drawPoint(50, 50, 2);
		context.drawRectangle(100, 100, 20, 20);
		context.write();
		context.display();
		
		// on screen
//		display(new File("/Users/sean_7/projects/FacePlusPlus/pics/test.jpg"));
		
		System.out.println("END");
	}

	
	
}
