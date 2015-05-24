/**
 * @Description TODO
 * @Author Wang Qi
 * @Date May 21, 2015
 */
package com.seandot7.facepp;

import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;


public class CameraCapture {

	private int period = 200;	// default 25
	private String imageFilePath = "./temp.jpg";
	private OpenCVFrameGrabber grabber;
	private OpenCVFrameConverter.ToIplImage converter;
	private Frame frame;
	private IplImage image;
	private int width;
	private int height;
	private BufferedImage bufferedImage;
	private Timer timer;
	private TimerTask timerTask;
	private int scaledWidth = 600;
	private int scaledHeight = 400;
	
	public int getPeriod() {
		return this.period;
	}
	
	public void setPeriod(int millisecond) {
		this.period = millisecond;
	}
	
	public String getImageFilePath() {
		return this.imageFilePath;
	}
	
	public void setImageFilePath(String imageFilePath) {
		this.imageFilePath = imageFilePath;
	}
	
	public Timer getTimer() {
		return this.timer;
	}
	
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

	// interface
	public BufferedImage getBufferedImageOfFrame() {
		return this.bufferedImage;
	}
	
	public byte[] getBytesImageOfFrame() {
		if (bufferedImage != null) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try {
				ImageIO.write(bufferedImage, "jpg", outputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] imageBytes = outputStream.toByteArray();
			return imageBytes;
		} else
			return null;
	}
	
	public Image getScaledImageOfFrame() {
		if (this.bufferedImage != null) {
			ImageIcon icon = new ImageIcon(this.bufferedImage);
			return icon.getImage().getScaledInstance(this.scaledWidth, this.scaledHeight, Image.SCALE_FAST);
		} else 
			return null;
	}
	
	public void on() {
		try {
			//open camera source
			grabber = new OpenCVFrameGrabber(0);
			grabber.start();
			//create a frame for real-time image display
			converter = new OpenCVFrameConverter.ToIplImage();
			
			frame = grabber.grab();
			image = converter.convert(frame);
			width = image.width();
			height = image.height();
			//onscreen buffer for image capture
			bufferedImage = new BufferedImage(width, height, Java2DFrameConverter.getBufferedImageType(frame));
			Java2DFrameConverter.copy(frame, bufferedImage);
			
			timerTask = new TimerTask() {
				
				@Override
				public void run() {
//					System.out.println("task 1");
					try {
						frame = grabber.grab();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					image = converter.convert(frame);
					Java2DFrameConverter.copy(frame, bufferedImage);
				}
			};
			timer = new Timer();
			timer.schedule(timerTask, 0, this.period);
	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void off() {
		//release resources
		try {
			timer.cancel();
			grabber.stop();
			cvReleaseImage(image);	 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pause() {
		this.timer.cancel();
	}
	
	public void resume() {
		this.timer.schedule(timerTask, 0, this.period);
	}
	
	public void saveAsImageFile() throws IOException {
		if (bufferedImage != null) {
			Image tempImage = this.bufferedImage.getScaledInstance(scaledWidth, scaledHeight, BufferedImage.SCALE_DEFAULT);
			BufferedImage tempBufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D tempGraphics2D = tempBufferedImage.createGraphics();
			tempGraphics2D.drawImage(tempImage, 0, 0, null);
			tempGraphics2D.dispose();
			ImageIO.write(tempBufferedImage, "jpg", new File(this.imageFilePath));
		}
	}
	
	public static void main(String[] args) {
		final CameraCapture cameraCapture = new CameraCapture();
		cameraCapture.on();
		cameraCapture.setImageFilePath("/Users/sean_7/projects/test_resource/temp.jpg");
		cameraCapture.off();
	}

}
