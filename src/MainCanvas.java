import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Canvas to use with swing
 */
public class MainCanvas extends Canvas {

	//Our frame buffer that stores all the image data
	private FrameBuffer buffer;
	private BufferedImage image;
	int width;
	int height;

	public MainCanvas(FrameBuffer buffer){
		super();
		this.buffer = buffer;
		this.width = buffer.getWidth();
		this.height = buffer.getHeight();
		this.setSize(buffer.getWidth(), buffer.getHeight());

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	//Need to override the paint function to use our own frame buffer pixels
	@Override
		public void paint(Graphics g){ 
			image.setRGB(0, 0, width, height, this.buffer.getPixels(), 0, width);
			g.drawImage(image, 0, 0, width, height, this);
		}

	//Save function so we can store the output
	public void save(String fileName) {
		try {
			ImageIO.write(image, "bmp", new File(fileName));
		} catch (IOException e) {
			System.out.println("Error: could not write to file " + fileName);
		}
	}
}
