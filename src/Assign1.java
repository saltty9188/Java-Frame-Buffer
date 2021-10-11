import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Assign1 {

	public static void main(String[] args) {

		System.out.println("Starting 2D engine...");
		// Buffer, Canvas and Frame containers.
		FrameBuffer buffer = null;
		MainCanvas canvas = null;
		JFrame frame = null;

		// Tools to read command line and text file instructions
		BufferedReader reader = null;
		Scanner scanner;
		String command;
		int lineNumber = 0;
		String trimmedLine;

		boolean debugging = false;

		// Look at each file
		for (int i = 0; i < args.length; i++) {
			try {

				// Open file and read line
				if (args[i].equals("-debug")) {
					debugging = true;
				} else {
					reader = new BufferedReader(new FileReader(args[i]));
					String line = reader.readLine();

					// Process and read every line
					while (line != null) {

						// Clean up any extra spaces etc.
						trimmedLine = line;
						while (trimmedLine != null && trimmedLine.trim().equals("")) {
							line = reader.readLine();
							trimmedLine = line;
						}
						lineNumber++;

						// ignore empty
						if (line == null) {
							break;
						}

						// Filter comments
						if (line.contains("#")) {
							String noComments;
							noComments = line.substring(0, line.indexOf("#"));
							// if there is text before comment, process it, otherwise ignore the line
							if (noComments.length() > 0) {
								line = noComments;
							} else {
								line = reader.readLine();
								continue;
							}
						}

						if (line.contains("(")) {
							// strip spaces from after opening bracket - ensure scanner reads RGB values
							// together with next()
							line = line.substring(0, line.indexOf("("))
									+ line.substring(line.indexOf("("), line.length()).replace(" ", "");
						}

						// Store command
						command = line.substring(0, line.indexOf(" "));

						// Read all the arguments into a scanner
						scanner = new Scanner(line.substring(line.indexOf(" "), line.length()));

						String color;
						String[] colors;
						int red;
						int blue;
						int green;
						int alpha;
						int x1;
						int y1;
						int x2;
						int y2;
						int xc;
						int yc;
						int radius;
						ArrayList<Integer> points;

						switch (command) {
						case "INIT":
							// Create the buffer and canvas
							int width = scanner.nextInt();
							int height = scanner.nextInt();
							buffer = new FrameBuffer(width, height);
							canvas = new MainCanvas(buffer);

							// Set up a swing frame
							frame = new JFrame();
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setSize(width, height);
							frame.add(canvas);
							frame.setTitle("Assign1");
							frame.setVisible(true);

							if(debugging) {
								System.out.println("Creating canvas, frame and buffer of size " + width + "x" + height);
							}
							break;

						case "POINT":
							// Get x and y location
							xc = scanner.nextInt();
							yc = scanner.nextInt();

				
							color = scanner.next();
							
							// Remove parentheses
							color = color.substring(1, color.length() - 1);
							// Split by the commas
							colors = color.split(",");

							// Store colour values
							red = Integer.parseInt(colors[0]);
							green = Integer.parseInt(colors[1]);
							blue = Integer.parseInt(colors[2]);
							alpha = Integer.parseInt(colors[3]);

							// Draw a point with our point method
							buffer.point(xc, yc, red, green, blue, alpha);
							canvas.repaint();

							if(debugging) {
								System.out.println("Drawing point: (" + xc + "," + yc + ")");
								System.out.println("R = " + red + " G = "+green + " B = "+blue);
							}

							break;
						case "LINE_FLOAT":

							// read end point values from next 4 ints
							x1 = scanner.nextInt();
							y1 = scanner.nextInt();
							x2 = scanner.nextInt();
							y2 = scanner.nextInt();

							color = scanner.next();
							// Remove parentheses
							color = color.substring(1, color.length() - 1);
							// Split by the commas
							colors = color.split(",");

							// Store colour values
							red = Integer.parseInt(colors[0]);
							green = Integer.parseInt(colors[1]);
							blue = Integer.parseInt(colors[2]);
							alpha = Integer.parseInt(colors[3]);

							// Draw a float line
							buffer.lineFloat(x1, y1, x2, y2, red, green, blue, alpha);
							canvas.repaint();

							if(debugging) {
								System.out.println("Drawing line float from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
								System.out.println("R = " + red + " G = " + green + " B = " + blue);
							}

							break;

						case "LINE":
							// read end point values from next 4 ints
							x1 = scanner.nextInt();
							y1 = scanner.nextInt();
							x2 = scanner.nextInt();
							y2 = scanner.nextInt();

							color = scanner.next();
							
							// Remove parentheses
							color = color.substring(1, color.length() - 1);
							// Split by the commas
							colors = color.split(",");

							// Store colour values
							red = Integer.parseInt(colors[0]);
							green = Integer.parseInt(colors[1]);
							blue = Integer.parseInt(colors[2]);
							alpha = Integer.parseInt(colors[3]);

							// Draw a float line
							buffer.line(x1, y1, x2, y2, red, green, blue, alpha);
							canvas.repaint();

							if(debugging) {
								System.out.println("Drawing line using Bresenham's line algorithm from (" + x1 + "," + y1
										+ ") to (" + x2 + "," + y2 + ")");
								System.out.println("R = " + red + " G = " + green + " B = " + blue);
							}
							break;
						case "OUTLINE_POLYGON":

							// Store co-ordiante values in an array
							points = new ArrayList<Integer>();
							while (scanner.hasNextInt()) {
								points.add(scanner.nextInt());
							}

							// Throw an exception if there is a mismatched x,y co-ordinate pair
							if (points.size() % 2 != 0) {
								throw new IllegalArgumentException("Mismatched co-ordinate pair.");
							}

							color = scanner.next();
							// Remove parentheses
							color = color.substring(1, color.length() - 1);
							// Split by the commas
							colors = color.split(",");

							// Store colour values
							red = Integer.parseInt(colors[0]);
							green = Integer.parseInt(colors[1]);
							blue = Integer.parseInt(colors[2]);
							alpha = Integer.parseInt(colors[3]);

							
							buffer.outlinePolygon(points.toArray(new Integer[0]), red, green, blue, alpha);
							canvas.repaint();

							if(debugging) {
								System.out.print("Drawing a polygon connecting points: ");
								for (int j = 0; j < points.size(); j += 2) {
									System.out.print("(" + points.get(j) + "," + points.get(j + 1) + ") ");
									if (j == points.size() - 4) {
										System.out.print("and ");
									}
								}
	
								System.out.println("\nR = " + red + " G = " + green + " B = " + blue);
							}

							break;
						case "FILL_POLYGON":

							// Store co-ordiante values in an array
							points = new ArrayList<Integer>();
							while (scanner.hasNextInt()) {
								points.add(scanner.nextInt());
							}

							// Throw an exception if there is a mismatched x,y co-ordinate pair
							if (points.size() % 2 != 0) {
								throw new IllegalArgumentException("Mismatched co-ordinate pair.");
							}

							color = scanner.next();
							// System.out.println(color);
							// Remove parentheses
							color = color.substring(1, color.length() - 1);
							// Split by the commas
							colors = color.split(",");

							// Store colour values
							red = Integer.parseInt(colors[0]);
							green = Integer.parseInt(colors[1]);
							blue = Integer.parseInt(colors[2]);
							alpha = Integer.parseInt(colors[3]);

							
							buffer.fillPolygon(points.toArray(new Integer[0]), red, green, blue, alpha);
							canvas.repaint();

							if(debugging) {
								System.out.print("Drawing a filled polygon connecting points: ");
								for (int j = 0; j < points.size(); j += 2) {
									System.out.print("(" + points.get(j) + "," + points.get(j + 1) + ") ");
									if (j == points.size() - 4) {
										System.out.print("and ");
									}
								}
	
								System.out.println("\nR = " + red + " G = " + green + " B = " + blue);
							}

							break;
						case "OUTLINE_CIRCLE":

							// Get the center point and the radius
							xc = scanner.nextInt();
							yc = scanner.nextInt();
							radius = scanner.nextInt();

							color = scanner.next();
							
							// Remove parentheses
							color = color.substring(1, color.length() - 1);
							// Split by the commas
							colors = color.split(",");

							// Store colour values
							red = Integer.parseInt(colors[0]);
							green = Integer.parseInt(colors[1]);
							blue = Integer.parseInt(colors[2]);
							alpha = Integer.parseInt(colors[3]);

							buffer.outlineCircle(xc, yc, radius, red, green, blue, alpha);
							canvas.repaint();

							if(debugging) {
								System.out.println("Drawing a circle with radius " + radius + " centered at point (" + xc
										+ "," + yc + ")");
								System.out.println("R = " + red + " G = " + green + " B = " + blue);
							}
							break;
						case "FILL_CIRCLE":
							// Get the center point and the radius
							xc = scanner.nextInt();
							yc = scanner.nextInt();
							radius = scanner.nextInt();

							color = scanner.next();
							
							// Remove parentheses
							color = color.substring(1, color.length() - 1);
							// Split by the commas
							colors = color.split(",");

							// Store colour values
							red = Integer.parseInt(colors[0]);
							green = Integer.parseInt(colors[1]);
							blue = Integer.parseInt(colors[2]);
							alpha = Integer.parseInt(colors[3]);

							buffer.fillCircle(xc, yc, radius, red, green, blue, alpha);
							canvas.repaint();

							if(debugging) {
								System.out.println("Drawing a filled circle with radius " + radius + " centered at point ("
										+ xc + "," + yc + ")");
								System.out.println("R = " + red + " G = " + green + " B = " + blue);
							}

							break;

						case "LOAD_PNG":
							
							// Get the file path					
							String filepath = scanner.next();
							BufferedImage img = ImageIO.read(new File(filepath));

							// Check if user wants to save PNG to a txt file
							boolean saveToFile = (scanner.hasNext() && scanner.next().equalsIgnoreCase("-save"));
							String textFileName = "";
							if (saveToFile) {
								textFileName = filepath.replace(".png", ".txt");
							}

							// Load the image)
							buffer.loadImage(img, saveToFile, textFileName);
							canvas.repaint();

							if(debugging) {
								System.out.println("Loading PNG Image \"" + filepath + "\"");
								if(saveToFile) {
									System.out.println("Saving to \"" + filepath.replace("png", "txt") + "\"");
								}
							}

							break;

						case "CROP":
							// Get the crop width, height, and center co-ordinates (if supplied)
							int cropWidth = scanner.nextInt();
							int cropHeight = scanner.nextInt();
							int centerX = 0;
							int centerY = 0;
							boolean centerPoint = false;

							// Check if user specified point for the crop to be centered on
							if (scanner.hasNextInt()) {
								centerX = scanner.nextInt();
								// check if there is a y co-ordinate to match
								if (!scanner.hasNextInt()) {
									throw new IllegalArgumentException("Mismatched co-ordinate pair.");
								} else {
									centerY = scanner.nextInt();
									centerPoint = true;
								}
							}

							// If center point was defined use it otherwise the default is the center of the
							// canvas
							if (centerPoint) {
								buffer.crop(cropWidth, cropHeight, centerX, centerY);
							} else {
								buffer.crop(cropWidth, cropHeight);
							}

							canvas.repaint();
							
							if(debugging) {
								System.out.println("Cropping the screen so that it is now " + cropWidth + "x" + cropHeight
										+ (centerPoint ? " centered at the point (" + centerX + "," + centerY + ")" : ""));
							}

							break;
						case "PAUSE":
							int millis = scanner.nextInt();
							if(debugging) {
								System.out.println("Pause: " + millis + " milliseconds");
							}
							try {
								Thread.sleep(millis);
							} catch (InterruptedException e) {
								System.out.println("Problem with sleep...");
								e.printStackTrace();
							}
							break;
						case "SAVE":

							String fileName = scanner.next();
							canvas.save(fileName);
							if(debugging) {
								System.out.println("Saving file:" + fileName);
							}

							break;
						default:
							System.out.println("Unknown command at line:  " + lineNumber);
						}
						line = reader.readLine();

					}

				}
			} catch (FileNotFoundException e) {
				System.out.println("Error: File could not be found: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("Error: Unexpected IO exception encountered");
			} catch (IllegalArgumentException e) {
				System.out.println("Error: File contained an illegal argument: " + e.getMessage());
			}

		}

	}
}
