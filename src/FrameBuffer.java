import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Frame buffer class skeleton
 */

public class FrameBuffer {

	// Store all pixel data in this int array.
	// NOTE: 1 int should contain red, green and blue data NOT 3!
	private int[] pixels;
	private int width;
	private int height;

	// Set up memory for pixel data
	public FrameBuffer(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
	}

	public void point(int xc, int yc, int r, int g, int b, int a) {
		// PIXEL = 0xAARRGGBB
		if (xc >= 0 && xc < width && yc >= 0 && yc < height) {
			pixels[xc + width * yc] = blend((float) a / 255.0f, (float) getAlpha(xc, yc) / 255.0f, r, getRed(xc, yc), g,
					getGreen(xc, yc), b, getBlue(xc, yc));
		}

	}

	/**
	 * Draws a straight line between two given points using floating point
	 * arithmetic of the form y = mx + c. Colour is determined by the given R,B,G,A
	 * values
	 * 
	 * @param x1 The first x co-ordinate.
	 * @param y1 The first y co-ordinate.
	 * @param x2 The second x co-ordinate.
	 * @param y2 The second y co-ordinate.
	 * @param r  The 8-bit red channel for the line.
	 * @param g  The 8-bit green channel for the line.
	 * @param b  The 8-bit blue channel for the line.
	 * @param a  The 8-bit alpha channel for the line.
	 */
	public void lineFloat(int x1, int y1, int x2, int y2, int r, int g, int b, int a) {

		// Line clipping
		int E1 = (y1 < 0 ? 1 << 3 : 0 << 3) | (y1 >= height ? 1 << 2 : 0 << 2) | (x1 < 0 ? 1 << 1 : 0 << 1)
				| (x1 >= width ? 1 : 0);
		int E2 = (y2 < 0 ? 1 << 3 : 0 << 3) | (y2 >= height ? 1 << 2 : 0 << 2) | (x2 < 0 ? 1 << 1 : 0 << 1)
				| (x2 >= width ? 1 : 0);

		if ((E1 & E2) != 0) {
			// Line is completely out of bounds so don't draw it
			return;
		} else if ((E1 | E2) != 0) {
			// Line partially out of bounds so clip it

			float m = (float) (y2 - y1) / (float) (x2 - x1);
			float c = y1 - m * x1;

			// E1 clip top, bottom, left then right
			if (((E1 >> 3) & 0b0001) == 1) {
				y1 = 0;
				x1 = (int) ((1 / m) * (y1 - c));
				E1 = (y1 < 0 ? 1 << 3 : 0 << 3) | (y1 >= height ? 1 << 2 : 0 << 2) | (x1 < 0 ? 1 << 1 : 0 << 1)
						| (x1 >= width ? 1 : 0);
			}
			if (((E1 >> 2) & 0b0001) == 1) {
				y1 = height - 1;
				x1 = (int) ((1 / m) * (y1 - c));
				E1 = (y1 < 0 ? 1 << 3 : 0 << 3) | (y1 >= height ? 1 << 2 : 0 << 2) | (x1 < 0 ? 1 << 1 : 0 << 1)
						| (x1 >= width ? 1 : 0);
			}
			if (((E1 >> 1) & 0b0001) == 1) {
				x1 = 0;
				y1 = (int) (m * x1 + c);
				E1 = (y1 < 0 ? 1 << 3 : 0 << 3) | (y1 >= height ? 1 << 2 : 0 << 2) | (x1 < 0 ? 1 << 1 : 0 << 1)
						| (x1 >= width ? 1 : 0);
			}
			if ((E1 & 0b0001) == 1) {
				x1 = width - 1;
				y1 = (int) (m * x1 + c);
			}

			// E2 clip top, bottom, left then right
			if (((E2 >> 3) & 0b0001) == 1) {
				y2 = 0;
				x2 = (int) ((1 / m) * (y2 - c));
				E2 = (y2 < 0 ? 1 << 3 : 0 << 3) | (y2 >= height ? 1 << 2 : 0 << 2) | (x2 < 0 ? 1 << 1 : 0 << 1)
						| (x2 >= width ? 1 : 0);
			}
			if (((E2 >> 2) & 0b0001) == 1) {
				y2 = height - 1;
				x2 = (int) ((1 / m) * (y2 - c));
				E2 = (y2 < 0 ? 1 << 3 : 0 << 3) | (y2 >= height ? 1 << 2 : 0 << 2) | (x2 < 0 ? 1 << 1 : 0 << 1)
						| (x2 >= width ? 1 : 0);
			}
			if (((E2 >> 1) & 0b0001) == 1) {
				x2 = 0;
				y2 = (int) (m * x2 + c);
				E2 = (y2 < 0 ? 1 << 3 : 0 << 3) | (y2 >= height ? 1 << 2 : 0 << 2) | (x2 < 0 ? 1 << 1 : 0 << 1)
						| (x2 >= width ? 1 : 0);
			}
			if ((E2 & 0b0001) == 1) {
				x2 = width - 1;
				y2 = (int) (m * x2 + c);
			}

		}

		// X values are equal, same point or vertical line
		if (x1 == x2) {
			// Both points are the same
			if (y1 == y2) {
				point(x1, y1, r, g, b, a);
			} else { // line is vertical (i.e m = infinity)

				// Step i positively if y1 < y2, step it negatively if y1 > y2
				for (int i = y1; (y1 < y2 ? i <= y2 : i >= y2); i += (y1 < y2 ? 1 : -1)) {
					point(x1, i, r, g, b, a);
				}
			}
		} else {

			// Calculate gradient
			float m = (float) (y2 - y1) / (float) (x2 - x1);

			// calculate offset
			float c = y1 - m * x1;

			// if |m| is greater than 1 then derive the line's x values from the y values
			if (m > 1 || m < -1) {

				// Step i positively if y1 < y2, step it negatively if y1 > y2
				for (int i = y1; (y1 < y2 ? i <= y2 : i >= y2); i += (y1 < y2 ? 1 : -1)) {
					point(Math.round((i - c) / m), i, r, g, b, a);
				}

			} else { // Otherwise derive the line's y value from the x values

				// Step i positively if x1 < x2, step it negatively if x1 > x2
				for (int i = x1; (x1 < x2 ? i <= x2 : i >= x2); i += (x1 < x2 ? 1 : -1)) {
					point(i, Math.round(m * i + c), r, g, b, a);
				}
			}
		}
	}

	/**
	 * Draws a straight line between two given points using Bresenham's line
	 * algorithm.
	 * 
	 * @param x1 The first x co-ordinate.
	 * @param y1 The first y co-ordinate.
	 * @param x2 The second x co-ordinate.
	 * @param y2 The second y co-ordinate.
	 * @param r  The 8-bit red channel for the line.
	 * @param g  The 8-bit green channel for the line.
	 * @param b  The 8-bit blue channel for the line.
	 * @param a  The 8-bit alpha channel for the line.
	 */
	public void line(int x1, int y1, int x2, int y2, int r, int g, int b, int a) {

		// Line clipping
		int E1 = (y1 < 0 ? 1 << 3 : 0 << 3) | (y1 >= height ? 1 << 2 : 0 << 2) | (x1 < 0 ? 1 << 1 : 0 << 1)
				| (x1 >= width ? 1 : 0);
		int E2 = (y2 < 0 ? 1 << 3 : 0 << 3) | (y2 >= height ? 1 << 2 : 0 << 2) | (x2 < 0 ? 1 << 1 : 0 << 1)
				| (x2 >= width ? 1 : 0);

		if ((E1 & E2) != 0) {
			// Line is completely out of bounds so don't draw it
			return;
		} else if ((E1 | E2) != 0) {
			// Line partially out of bounds so clip it

			float m = (float) (y2 - y1) / (float) (x2 - x1);
			float c = y1 - m * x1;

			// E1 clip top, bottom, left then right
			if (((E1 >> 3) & 0b0001) == 1) {
				y1 = 0;
				x1 = (int) ((1 / m) * (y1 - c));
				E1 = (y1 < 0 ? 1 << 3 : 0 << 3) | (y1 >= height ? 1 << 2 : 0 << 2) | (x1 < 0 ? 1 << 1 : 0 << 1)
						| (x1 >= width ? 1 : 0);
			}
			if (((E1 >> 2) & 0b0001) == 1) {
				y1 = height - 1;
				x1 = (int) ((1 / m) * (y1 - c));
				E1 = (y1 < 0 ? 1 << 3 : 0 << 3) | (y1 >= height ? 1 << 2 : 0 << 2) | (x1 < 0 ? 1 << 1 : 0 << 1)
						| (x1 >= width ? 1 : 0);
			}
			if (((E1 >> 1) & 0b0001) == 1) {
				x1 = 0;
				y1 = (int) (m * x1 + c);
				E1 = (y1 < 0 ? 1 << 3 : 0 << 3) | (y1 >= height ? 1 << 2 : 0 << 2) | (x1 < 0 ? 1 << 1 : 0 << 1)
						| (x1 >= width ? 1 : 0);
			}
			if ((E1 & 0b0001) == 1) {
				x1 = width - 1;
				y1 = (int) (m * x1 + c);
			}

			// E2 clip top, bottom, left then right
			if (((E2 >> 3) & 0b0001) == 1) {
				y2 = 0;
				x2 = (int) ((1 / m) * (y2 - c));
				E2 = (y2 < 0 ? 1 << 3 : 0 << 3) | (y2 >= height ? 1 << 2 : 0 << 2) | (x2 < 0 ? 1 << 1 : 0 << 1)
						| (x2 >= width ? 1 : 0);
			}
			if (((E2 >> 2) & 0b0001) == 1) {
				y2 = height - 1;
				x2 = (int) ((1 / m) * (y2 - c));
				E2 = (y2 < 0 ? 1 << 3 : 0 << 3) | (y2 >= height ? 1 << 2 : 0 << 2) | (x2 < 0 ? 1 << 1 : 0 << 1)
						| (x2 >= width ? 1 : 0);
			}
			if (((E2 >> 1) & 0b0001) == 1) {
				x2 = 0;
				y2 = (int) (m * x2 + c);
				E2 = (y2 < 0 ? 1 << 3 : 0 << 3) | (y2 >= height ? 1 << 2 : 0 << 2) | (x2 < 0 ? 1 << 1 : 0 << 1)
						| (x2 >= width ? 1 : 0);
			}
			if ((E2 & 0b0001) == 1) {
				x2 = width - 1;
				y2 = (int) (m * x2 + c);
			}

		}

		// X values are equal, either both the same point or a vertical line
		if (x1 == x2) {
			if (y1 == y2) {
				point(x1, y1, r, g, b, a);
			} else {
				// Step i positively if y1 < y2, step it negatively if y1 > y2
				for (int y = y1; (y1 < y2 ? y <= y2 : y >= y2); y += (y1 < y2 ? 1 : -1)) {
					point(x1, y, r, g, b, a);
				}
			}
		} else if (y1 == y2) { // Y values are equal, either both the same point or a horizontal line
			if (x1 == x2) {
				point(x1, y1, r, g, b, a);
			} else {
				// Step i positively if x1 < x2, step it negatively if x1 > x2
				for (int x = x1; (x1 < x2 ? x <= x2 : x >= x2); x += (x1 < x2 ? 1 : -1)) {
					point(x, y1, r, g, b, a);
				}
			}
		} else { // diagonal line, use Bresenham's line algorithm
			int x = 0;
			int y = 0;
			int p = 0;
			int deltaX = x2 - x1;
			int deltaY = y2 - y1;

			// Check if deltaY is greater than deltaX
			if (Math.abs(deltaY) > Math.abs(deltaX)) {
				// Check that y1 is less than y2 and swap them if it's not
				if (y1 > y2) {
					// Use bitwise XOR to swap variables without making a temporary variable
					// from
					// https://www.tutorialspoint.com/swap-two-variables-in-one-line-in-java#:~:text=In%20order%20to%20swap%20two,of%20a%20and%20b%20differs.
					x1 = x1 ^ x2 ^ (x2 = x1);
					y1 = y1 ^ y2 ^ (y2 = y1);
					// recalculate deltaX and deltaY
					deltaX = x2 - x1;
					deltaY = y2 - y1;
				}

				x = x1;
				y = y1;
				point(x, y, r, g, b, a);

				// If deltaX is negative swap the signs of the algorithm to allow the offset to
				// move the other way
				// Still increment y positively as it is guaranteed to be positive
				if (deltaX < 0) {
					p = 2 * deltaX - deltaY;
					int twoDx = 2 * deltaX;
					int twoDxPlusDy = 2 * (deltaX + deltaY);

					while (deltaY != 0) {
						y++;
						if (p < 0) {
							p -= twoDx;
						} else {
							x--;
							p -= twoDxPlusDy;
						}
						deltaY--;
						point(x, y, r, g, b, a);
					}
				} else {
					p = 2 * deltaX - deltaY;
					int twoDx = 2 * deltaX;
					int twoDxMinusDy = 2 * (deltaX - deltaY);

					while (deltaY != 0) {
						y++;
						if (p < 0) {
							p += twoDx;
						} else {
							x++;
							p += twoDxMinusDy;
						}
						deltaY--;
						point(x, y, r, g, b, a);
					}
				}
			} else { // deltaX > deltaY
				// Check that x1 is less than x2 and swap them if it's not
				if (x1 > x2) {
					// Use bitwise XOR to swap variables without making a temporary variable
					// from
					// https://www.tutorialspoint.com/swap-two-variables-in-one-line-in-java#:~:text=In%20order%20to%20swap%20two,of%20a%20and%20b%20differs.
					x1 = x1 ^ x2 ^ (x2 = x1);
					y1 = y1 ^ y2 ^ (y2 = y1);
					// recalculate deltaX and deltaY
					deltaX = x2 - x1;
					deltaY = y2 - y1;

				}
				x = x1;
				y = y1;
				point(x, y, r, g, b, a);

				// If deltaY is negative swap the signs of the algorithm to allow the offset to
				// move the other way
				// Still increment x positively as it is guaranteed to be positive
				if (deltaY < 0) {
					p = 2 * deltaY - deltaX;
					int twoDy = 2 * deltaY;
					int twoDyPlusDx = 2 * (deltaY + deltaX);
					while (deltaX != 0) {
						x++;
						if (p < 0) {
							p -= twoDy;
						} else {
							y--;
							p -= twoDyPlusDx;
						}
						deltaX--;
						point(x, y, r, g, b, a);
					}
				} else {
					p = 2 * deltaY - deltaX;
					int twoDy = 2 * deltaY;
					int twoDyMinusDx = 2 * (deltaY - deltaX);

					while (deltaX != 0) {
						x++;
						if (p < 0) {
							p += twoDy;
						} else {
							y++;
							p += twoDyMinusDx;
						}
						deltaX--;
						point(x, y, r, g, b, a);
					}
				}
			}
		}

	}

	/**
	 * Outlines an arbitrary polygon defined py the x and y co-ordinates contained
	 * within the given array
	 * 
	 * @param points An array of Integers with x and y co-ordinates at indices 2n
	 *               and 2n+1 respectively.
	 * @param r      The 8-bit red channel for the polygon.
	 * @param g      The 8-bit green channel for the polygon.
	 * @param b      The 8-bit blue channel for the polygon.
	 * @param a      The 8-bit alpha channel for the polygon.
	 */
	public void outlinePolygon(Integer[] points, int r, int g, int b, int a) {

		for (int i = 0; i < points.length; i += 2) {
			// If we're at the last point connect to the first point
			if (i >= points.length - 2) {
				line(points[i], points[i + 1], points[0], points[1], r, g, b, a);
			} else {
				// Draw a line from this point to the next set
				line(points[i], points[i + 1], points[i + 2], points[i + 3], r, g, b, a);
			}
		}
	}

	public void fillPolygon(Integer[] points, int r, int g, int b, int a) {

		// Temporary buffer to store where points will be drawn.
		// Used so that previous points don't interfere with the the scan line and so
		// that overlapping
		// points don't result in incorrect alpha blending
		byte[] pointsToDraw = new byte[pixels.length];

		for (int i = 0; i < points.length; i += 2) {
			// If we're at the last point connect to the first point
			if (i >= points.length - 2) {
				storeLine(points[i], points[i + 1], points[0], points[1], pointsToDraw);
			} else {
				// Draw a line from this point to the next set
				storeLine(points[i], points[i + 1], points[i + 2], points[i + 3], pointsToDraw);
			}
		}

		boolean drawing = false;
		boolean onEdge = false;
		boolean edgeFound = false;
		int edgeStart = 0;

		// -----------------------------------------------------------------------------------------------------------
		// Check if any of the x points are out of bounds
		boolean outOfBounds = false;
		for (int i = 0; i < points.length; i += 2) {
			if (points[i] < 0 || points[i] >= width) {
				outOfBounds = true;
			}
		}

		// Scan the left and right edges of the buffer to fill in the cut off edges
		if (outOfBounds) {
			for (int i = 0; i < pointsToDraw.length; i += width) {

				// x and y co-ordinates for this pixel
				int x = i % width;
				int y = (i - x) / width;

				// If this given pixel is active then you are on an edge
				// Ignore lines that overlap themselves except for vertices which can overlap
				// one time each
				if (pointsToDraw[i] == 1 || (isVertex(points, x, y) && pointsToDraw[i] == 2)) {
					if (!onEdge) {
						edgeStart = i;
						onEdge = true;
					}
				} else if (onEdge) { // First pixel off the edge
					// Start and end x and y co-ordinates for this edge block
					int sx = edgeStart % width;
					int sy = (edgeStart - sx) / width;
					int ex = (i - 1) % width;
					int ey = (i - ex) / width;

					if (isVertex(points, sx, sy) && isVertex(points, ex, ey) && (ey != sy)) {
						// find the x index for the previous point in the points array that was not
						// equal to this x co-ordinate
						int prevXIndex = getPreviousXIndex(points, ex, ey);
						while (points[prevXIndex] == ex) {
							if (prevXIndex == 0) {
								prevXIndex = points.length - 2;
							} else {
								prevXIndex -= 2;
							}
						}

						// find the y index for the next point in the points array that was not equal to
						// this y co-ordinate
						int nextXIndex = getNextXIndex(points, ex, ey);
						while (points[nextXIndex] == ex) {
							if (nextXIndex == points.length - 2) {
								nextXIndex = 0;
							} else {
								nextXIndex += 2;
							}
						}

						// If the next set of applicable vertices go in opposite direction we treat this
						// vertex like a normal edge
						if ((points[prevXIndex] < x && points[nextXIndex] > x)
								|| (points[prevXIndex] > x && points[nextXIndex] < x)) {
							drawing = !drawing;
						}

					} else if (isVertex(points, sx, sy)) {
						// Do the same for the next two blocks without the loop
						int prevXVert = points[getPreviousXIndex(points, sx, sy)];
						int nextXVert = points[getNextXIndex(points, sx, sy)];

						if ((prevXVert < x && nextXVert > x) || (prevXVert > x && nextXVert < x)) {
							drawing = !drawing;
						}

					} else if (isVertex(points, ex, ey)) {
						int prevXVert = points[getPreviousXIndex(points, ex, ey)];
						int nextXVert = points[getNextXIndex(points, ex, ey)];

						if ((prevXVert < x && nextXVert > x) || (prevXVert > x && nextXVert < x)) {
							drawing = !drawing;
						}
					} else {
						drawing = !drawing;
					}

					// Check adjacent "closing" edge
					if (drawing) {
						for (int j = i; j < pointsToDraw.length; j += width) {
							if (pointsToDraw[j] > 0) {
								edgeFound = true;
							}
						}

						if (!edgeFound) {
							drawing = false;
						} else {
							edgeFound = false;
						}
					}

					onEdge = false;
				}

				if (drawing) {
					pointsToDraw[i] = 1;
				}

				// at the end of the left column set i to be at the right column
				if (i == pointsToDraw.length - width) {
					i = width - 1;
					onEdge = false;
					edgeFound = false;
					drawing = false;
				}
			}
		}

		// ------------------------------------------------------------------------------------------------------------
		int edgeBlockWidth = 0;
		// Scan from left to right
		for (int i = 0; i < pointsToDraw.length; i++) {

			// x and y co-ordinates for this pixel
			int x = i % width;
			int y = (i - x) / width;

			// reset drawing and onEdge each line
			if (i % width == 0) {
				onEdge = false;
				edgeFound = false;
				drawing = false;
			}

			// If this given pixel is active then you are on an edge
			// Ignore pixels that overlap themselves an even amount of times except for
			// vertices
			if (pointsToDraw[i] % 2 == 1 || (isVertex(points, x, y) && pointsToDraw[i] % 2 == 0)) {
				if (!onEdge) {
					edgeStart = i;
					onEdge = true;
					edgeBlockWidth = 1;
				} else {
					edgeBlockWidth += 1;
				}

			} else if (onEdge) { // First pixel off the edge
				// Start and end x and y co-ordinates for this edge block
				int sx = edgeStart % width;
				int sy = (edgeStart - sx) / width;
				int ex = (i - 1) % width;
				int ey = (i - ex) / width;
				// Check for S, Z, U and n cases
				if (isVertex(points, sx, sy) && isVertex(points, ex, ey) && (ex != sx)) {
					// find the y index for the previous point in the points array that was not
					// equal to this y co-ordinate
					int prevYIndex = getPreviousXIndex(points, ex, ey) + 1;
					while (points[prevYIndex] == ey) {

						// If the previous vertex is overlapped an odd amount of times, skip it as it
						// likely doubles back on itself
						if (pointsToDraw[points[prevYIndex - 1] + width * points[prevYIndex]] % 2 == 1) {

							if (prevYIndex == 1) {
								prevYIndex = points.length - 1;
							} else {
								prevYIndex -= 2;
							}
						}

						if (prevYIndex == 1) {
							prevYIndex = points.length - 1;
						} else {
							prevYIndex -= 2;
						}
					}

					// find the y index for the next point in the points array that was not equal to
					// this y co-ordinate
					int nextYIndex = getNextXIndex(points, ex, ey) + 1;
					while (points[nextYIndex] == ey) {
						// If the next vertex is overlapped an odd amount of times, skip it as it likely
						// doubles back on itself
						if (pointsToDraw[points[nextYIndex - 1] + width * points[nextYIndex]] % 2 == 1) {

							if (nextYIndex == points.length - 1) {
								nextYIndex = 1;
							} else {
								nextYIndex += 2;
							}
						}

						if (nextYIndex == points.length - 1) {
							nextYIndex = 1;
						} else {
							nextYIndex += 2;
						}

					}

					// If the next set of applicable vertices go in opposite direction we treat this
					// vertex like a normal edge
					if ((points[prevYIndex] < y && points[nextYIndex] > y)
							|| (points[prevYIndex] > y && points[nextYIndex] < y)) {
						drawing = !drawing;
					}

				} else if (isVertex(points, sx, sy) && !onSeparateLines(sx, sy, ex, ey, pointsToDraw, points)) {
					// Do the same for the next two blocks without the loop
					int prevYVert = points[getPreviousXIndex(points, sx, sy) + 1];
					int nextYVert = points[getNextXIndex(points, sx, sy) + 1];

					if ((prevYVert < y && nextYVert > y) || (prevYVert > y && nextYVert < y)) {
						drawing = !drawing;
					}

				} else if (isVertex(points, ex, ey) && !onSeparateLines(sx, sy, ex, ey, pointsToDraw, points)) {
					int prevYVert = points[getPreviousXIndex(points, ex, ey) + 1];
					int nextYVert = points[getNextXIndex(points, ex, ey) + 1];

					if ((prevYVert < y && nextYVert > y) || (prevYVert > y && nextYVert < y)) {
						drawing = !drawing;
					}

				} else if ((edgeBlockWidth > 1 && !onSeparateLines(sx, sy, ex, ey, pointsToDraw, points))
						|| edgeBlockWidth == 1) {
					// If this edge block contains no vertices, and the start and end points are
					// either the same point (single pixel width) or
					// are both on the same line then toggle drawing state.
					drawing = !drawing;
				}

				// Check adjacent "closing" edge
				if (drawing) {
					int endLine = width - (i % width) + i;
					for (int j = i; j < endLine; j++) {
						if (pointsToDraw[j] > 0) {
							edgeFound = true;
						}
					}
					if (!edgeFound) {
						drawing = false;
					} else {
						edgeFound = false;
					}
				}

				onEdge = false;
			}

			if (drawing) {
				pointsToDraw[i] = 1;
			}
		}

		// Draw the points in the pointsToDraw array
		for (int i = 0; i < pointsToDraw.length; i++) {
			if (pointsToDraw[i] > 0) {
				int x = i % width;
				int y = (i - x) / width;
				point(x, y, r, g, b, a);
			}
		}
	}

	/**
	 * Draws a straight line between two given points using Bresenham's line
	 * algorithm. Stores the points where this line is to be drawn in a given Byte
	 * array for use with fill polygon. Increments each point in the byte array by
	 * one each time that is drawn to, to show where lines overlap themselves.
	 * 
	 * @param x1           The first x co-ordinate.
	 * @param y1           The first y co-ordinate.
	 * @param x2           The second x co-ordinate.
	 * @param y2           The second y co-ordinate.
	 * @param pointsToDraw The array that stores the points to be drawn.
	 */
	private void storeLine(int x1, int y1, int x2, int y2, byte[] pointsToDraw) {

		// X values are equal, either both the same point or a vertical line
		if (x1 == x2) {
			if (y1 == y2) {
				pointsToDraw[x1 + width * y1] += 1;
			} else {
				// Step i positively if y1 < y2, step it negatively if y1 > y2
				for (int y = y1; (y1 < y2 ? y <= y2 : y >= y2); y += (y1 < y2 ? 1 : -1)) {
					if (x1 >= 0 && x1 < width && y >= 0 && y < height) {
						pointsToDraw[x1 + width * y] += 1;
					}
				}
			}
		} else if (y1 == y2) { // Y values are equal, either both the same point or a horizontal line
			if (x1 == x2) {
				if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) {
					pointsToDraw[x1 + width * y1] += 1;
				}
			} else {
				// Step i positively if x1 < x2, step it negatively if x1 > x2
				for (int x = x1; (x1 < x2 ? x <= x2 : x >= x2); x += (x1 < x2 ? 1 : -1)) {
					if (x >= 0 && x < width && y1 >= 0 && y1 < height) {
						pointsToDraw[x + width * y1] += 1;
					}
				}
			}
		} else { // diagonal line, use Bresenham's line algorithm
			int x = 0;
			int y = 0;
			int p = 0;
			int deltaX = x2 - x1;
			int deltaY = y2 - y1;

			// Check if deltaY is greater than deltaX
			if (Math.abs(deltaY) > Math.abs(deltaX)) {
				// Check that y1 is less than y2 and swap them if it's not
				if (y1 > y2) {
					// Use bitwise XOR to swap variables without making a temporary variable
					// from
					// https://www.tutorialspoint.com/swap-two-variables-in-one-line-in-java#:~:text=In%20order%20to%20swap%20two,of%20a%20and%20b%20differs.
					x1 = x1 ^ x2 ^ (x2 = x1);
					y1 = y1 ^ y2 ^ (y2 = y1);
					// recalculate deltaX and deltaY
					deltaX = x2 - x1;
					deltaY = y2 - y1;
				}

				x = x1;
				y = y1;
				if (x >= 0 && x < width && y >= 0 && y < height) {
					pointsToDraw[x + width * y] += 1;
				}

				// If deltaX is negative swap the signs of the algorithm to allow the offset to
				// move the other way
				// Still increment y positively as it is guaranteed to be positive
				if (deltaX < 0) {
					p = 2 * deltaX - deltaY;
					int twoDx = 2 * deltaX;
					int twoDxPlusDy = 2 * (deltaX + deltaY);

					while (deltaY != 0) {
						y++;
						if (p < 0) {
							p -= twoDx;
						} else {
							x--;
							p -= twoDxPlusDy;
						}
						deltaY--;
						if (x >= 0 && x < width && y >= 0 && y < height) {
							pointsToDraw[x + width * y] += 1;
						}
					}
				} else {
					p = 2 * deltaX - deltaY;
					int twoDx = 2 * deltaX;
					int twoDxMinusDy = 2 * (deltaX - deltaY);

					while (deltaY != 0) {
						y++;
						if (p < 0) {
							p += twoDx;
						} else {
							x++;
							p += twoDxMinusDy;
						}
						deltaY--;
						if (x >= 0 && x < width && y >= 0 && y < height) {
							pointsToDraw[x + width * y] += 1;
						}
					}
				}
			} else { // deltaX > deltaY
				// Check that x1 is less than x2 and swap them if it's not
				if (x1 > x2) {
					// Use bitwise XOR to swap variables without making a temporary variable
					// from
					// https://www.tutorialspoint.com/swap-two-variables-in-one-line-in-java#:~:text=In%20order%20to%20swap%20two,of%20a%20and%20b%20differs.
					x1 = x1 ^ x2 ^ (x2 = x1);
					y1 = y1 ^ y2 ^ (y2 = y1);
					// recalculate deltaX and deltaY
					deltaX = x2 - x1;
					deltaY = y2 - y1;

				}
				x = x1;
				y = y1;
				if (x >= 0 && x < width && y >= 0 && y < height) {
					pointsToDraw[x + width * y] += 1;
				}

				// If deltaY is negative swap the signs of the algorithm to allow the offset to
				// move the other way
				// Still increment x positively as it is guaranteed to be positive
				if (deltaY < 0) {
					p = 2 * deltaY - deltaX;
					int twoDy = 2 * deltaY;
					int twoDyPlusDx = 2 * (deltaY + deltaX);
					while (deltaX != 0) {
						x++;
						if (p < 0) {
							p -= twoDy;
						} else {
							y--;
							p -= twoDyPlusDx;
						}
						deltaX--;
						if (x >= 0 && x < width && y >= 0 && y < height) {
							pointsToDraw[x + width * y] += 1;
						}
					}
				} else {
					p = 2 * deltaY - deltaX;
					int twoDy = 2 * deltaY;
					int twoDyMinusDx = 2 * (deltaY - deltaX);

					while (deltaX != 0) {
						x++;
						if (p < 0) {
							p += twoDy;
						} else {
							y++;
							p += twoDyMinusDx;
						}
						deltaX--;
						if (x >= 0 && x < width && y >= 0 && y < height) {
							pointsToDraw[x + width * y] += 1;
						}
					}
				}
			}
		}
	}

	/**
	 * Returns true if the the two sets of points lie on different lines.
	 * 
	 * @param x1           The first x co-ordinate.
	 * @param y1           The first y co-ordinate.
	 * @param x2           The second x co-ordinate.
	 * @param y2           The second y co-ordinate.
	 * @param pointsToDraw The array that stores the points drawn for this polygon.
	 * @param points       The array of points for the polygon.
	 * @return True if the the two sets of points lie on different lines.
	 */
	private boolean onSeparateLines(int x1, int y1, int x2, int y2, byte[] pointsToDraw, Integer[] points) {

		// check for adjacent vertical lines first
		int y = y1 - 1;
		// check above
		boolean leftVertexFound = false;
		boolean rightVertexFound = false;
		while (y >= 0 && (leftVertexFound || pointsToDraw[x1 + width * y] > 0)
				&& (rightVertexFound || pointsToDraw[x2 + width * y] > 0)) {
			if (isVertex(points, x1, y)) {
				leftVertexFound = true;
			}
			if (isVertex(points, x2, y)) {
				rightVertexFound = true;
			}
			y--;
		}
		if (leftVertexFound && rightVertexFound) {
			return true;
		}

		// check below
		y = y1;
		leftVertexFound = false;
		rightVertexFound = false;
		while (y < height && (leftVertexFound || pointsToDraw[x1 + width * y] > 0)
				&& (rightVertexFound || pointsToDraw[x2 + width * y] > 0)) {
			if (isVertex(points, x1, y)) {
				leftVertexFound = true;
			}
			if (isVertex(points, x2, y)) {
				rightVertexFound = true;
			}
			y++;
		}
		if (leftVertexFound && rightVertexFound) {
			return true;
		}

		int startX = x1 - 1;
		if (startX < 0) {
			startX = 0;
		}
		int endX = x2 + 1;
		if (endX >= width) {
			endX = width - 1;
		}
		int startY = y1 - 1;
		if (startY < 0) {
			startY = 0;
		}
		int endY = y1 + 1;
		if (endY >= height) {
			endY = height - 1;
		}

		boolean onEdge = false;
		boolean lookingForEdge = false;

		// Scan the line above and below and check if there is a line adjacent
		// from another line. Scan starts one pixel left of the left-most pixel and ends
		// at the right-most pixel.
		for (y = startY; y <= endY; y += 2) {
			for (int x = startX; x <= endX; x++) {
				if (pointsToDraw[x + width * y] > 0) {
					if (!onEdge) {
						onEdge = true;
					}
					// Opposite edge found
					if (lookingForEdge) {
						return true;
					}
				} else if (onEdge) {
					lookingForEdge = true;
					onEdge = false;
				}
			}
			onEdge = false;
			lookingForEdge = false;
		}
		return false;
	}

	/**
	 * Returns true if the the given point is present in the given array of vertices
	 * 
	 * @param points The array containing the set of vertices.
	 * @param x      The x value to be tested.
	 * @param y      The y value to be tested
	 * @return True if the given point is present in the array of vertices.
	 */
	private boolean isVertex(Integer[] points, int x, int y) {
		for (int i = 0; i < points.length; i += 2) {
			if (points[i] == x && points[i + 1] == y) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the index of the previous X co-ordinate from the given array of
	 * vertices relative to the given x and y points. Assumes that each X,Y pair is
	 * unique within the array.
	 * 
	 * @param points The array containing the set of vertices.
	 * @param x      The x value to be tested.
	 * @param y      The y value to be tested
	 * @return The index of the previous X point
	 */
	private int getPreviousXIndex(Integer[] points, int currentX, int currentY) {
		int currentXIndex = 0;
		for (int i = 0; i < points.length; i += 2) {
			if (points[i] == currentX && points[i + 1] == currentY) {
				currentXIndex = i;
			}
		}

		if (currentXIndex == 0) {
			return points.length - 2;
		} else {
			return currentXIndex - 2;
		}
	}

	/**
	 * Returns the index of the next X co-ordinate from the given array of vertices
	 * relative to the given x and y points. Assumes that each X,Y pair is unique
	 * within the array.
	 * 
	 * @param points The array containing the set of vertices.
	 * @param x      The x value to be tested.
	 * @param y      The y value to be tested
	 * @return The index of the next X point
	 */
	private int getNextXIndex(Integer[] points, int currentX, int currentY) {
		int currentXIndex = 0;
		for (int i = 0; i < points.length; i += 2) {
			if (points[i] == currentX && points[i + 1] == currentY) {
				currentXIndex = i;
			}
		}

		if (currentXIndex == points.length - 2) {
			return 0;
		} else {
			return currentXIndex + 2;
		}
	}

	/**
	 * Outlines a circle centered at the point (xc,yc) with the given radius.
	 * 
	 * @param xc     The x co-ordinate for the center of the circle.
	 * @param yc     The y co-ordinate for the center of the circle.
	 * @param radius The radius of the circle.
	 * @param r      The 8-bit red channel for the circle.
	 * @param g      The 8-bit green channel for the circle.
	 * @param b      The 8-bit blue channel for the circle.
	 * @param a      The 8-bit alpha channel for the circle.
	 */
	public void outlineCircle(int xc, int yc, int radius, int r, int g, int b, int a) {

		// Initial x and y co-ordinates as if the circle was centered at the origin
		int xo = 0;
		int yo = radius;
		int pk = 1 - radius;

		// Draw the first 8 points offset by the center
		point(xc + xo, yc + yo, r, g, b, a);
		point(xc - xo, yc + yo, r, g, b, a);
		point(xc + xo, yc - yo, r, g, b, a);
		point(xc - xo, yc - yo, r, g, b, a);
		point(xc + yo, yc + xo, r, g, b, a);
		point(xc - yo, yc + xo, r, g, b, a);
		point(xc + yo, yc - xo, r, g, b, a);
		point(xc - yo, yc - xo, r, g, b, a);

		// Use the midpoint algorithm
		while (xo < yo) {
			if (pk < 0) {
				pk += 2 * xo + 1;
				xo++;
			} else {
				pk += (xo - yo) * 2 + 1;
				xo++;
				yo--;
			}

			// Plot the 8 mirrored points around the circle
			point(xc + xo, yc + yo, r, g, b, a);
			point(xc - xo, yc + yo, r, g, b, a);
			point(xc + xo, yc - yo, r, g, b, a);
			point(xc - xo, yc - yo, r, g, b, a);
			point(xc + yo, yc + xo, r, g, b, a);
			point(xc - yo, yc + xo, r, g, b, a);
			point(xc + yo, yc - xo, r, g, b, a);
			point(xc - yo, yc - xo, r, g, b, a);

		}
	}

	/**
	 * Fills a circle centered at the point (xc,yc) with the given radius.
	 * 
	 * @param xc     The x co-ordinate for the center of the circle.
	 * @param yc     The y co-ordinate for the center of the circle.
	 * @param radius The radius of the circle.
	 * @param r      The 8-bit red channel for the circle.
	 * @param g      The 8-bit green channel for the circle.
	 * @param b      The 8-bit blue channel for the circle.
	 * @param a      The 8-bit alpha channel for the circle.
	 */
	public void fillCircle(int xc, int yc, int radius, int r, int g, int b, int a) {

		int xo = 0;
		int yo = radius;
		int pk = 1 - radius;

		// For each pair of points on the same row, fill in the rest of the row
		for (int i = xc - xo; i <= xc + xo; i++) {
			point(i, yc + yo, r, g, b, a);
		}

		for (int i = xc - xo; i <= xc + xo; i++) {
			point(i, yc - yo, r, g, b, a);
		}

		// This row only needs to be filled once (xo = 0 so yc - xo = yc + xo)
		for (int i = xc - yo; i <= xc + yo; i++) {
			point(i, yc + xo, r, g, b, a);
		}

		// Fill in the rest of the rows using the midpoint algorithm
		while (xo < yo) {
			if (pk < 0) {
				pk += 2 * xo + 1;
				xo++;
			} else {

				pk += (xo - yo) * 2 + 1;
				xo++;
				yo--;

				// Only draw these rows when yo changes as that affects the row that is being
				// drawn
				for (int i = xc - xo; i <= xc + xo; i++) {
					point(i, yc + yo, r, g, b, a);
				}

				for (int i = xc - xo; i <= xc + xo; i++) {
					point(i, yc - yo, r, g, b, a);
				}
			}

			// skip drawing the last row on these sets as it overlaps with an earlier row
			if (xo < yo) {
				// These have their rows changed by xo and must be drawn every iteration
				for (int i = xc - yo; i <= xc + yo; i++) {
					point(i, yc + xo, r, g, b, a);
				}

				for (int i = xc - yo; i <= xc + yo; i++) {
					point(i, yc - xo, r, g, b, a);
				}
			}
		}
	}

	/**
	 * Performs an alpha composite of the given sets of RGBA values
	 * 
	 * @param srcA  The alpha value of the source (foreground) pixel (between 0.0f
	 *              and 1.0f).
	 * @param destA The alpha value of the destination (background) pixel (between
	 *              0.0f and 1.0f).
	 * @param srcR  The red value of the source (foreground) pixel.
	 * @param destR The red value of the destination (background) pixel.
	 * @param srcG  The green value of the source (foreground) pixel.
	 * @param destG The green value of the destination (background) pixel.
	 * @param srcB  The blue value of the source (foreground) pixel.
	 * @param destB The blue value of the destination (background) pixel.
	 * @return The bit-packed value of the pixel;
	 */
	public int blend(float srcA, float destA, int srcR, int destR, int srcG, int destG, int srcB, int destB) {

		float fOutA = (srcA + destA * (1 - srcA));
		int outR = (int) ((srcR * srcA + destR * destA * (1 - srcA)) / fOutA);
		outR = outR << 16;

		int outG = (int) ((srcG * srcA + destG * destA * (1 - srcA)) / fOutA);
		outG = outG << 8;

		int outB = (int) ((srcB * srcA + destB * destA * (1 - srcA)) / fOutA);

		int iOutA = (int) (fOutA * 255);
		iOutA = iOutA << 24;

		return iOutA | outR | outG | outB;
	}

	/**
	 * Loads an image and displays it to the canvas.
	 * 
	 * @param img          The BufferedImage to be loaded.
	 * @param saveToFile   True if the image is to be saved to a txt file, false
	 *                     otherwise
	 * @param textFileName The name for the converted text file if applicable. Can
	 *                     be null or empty if the user does not wish to save as a
	 *                     text file.
	 */
	public void loadImage(BufferedImage img, boolean saveToFile, String textFileName) {
		// Center the image on the canvas
		int startX = width / 2 - img.getWidth() / 2;
		int startY = height / 2 - img.getHeight() / 2;

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {

				// Sample the image's rgba values
				int alpha = img.getRGB(x, y) >> 24 & 0xFF;
				int red = img.getRGB(x, y) >> 16 & 0xFF;
				int green = img.getRGB(x, y) >> 8 & 0xFF;
				int blue = img.getRGB(x, y) & 0xFF;
				point(startX + x, startY + y, red, green, blue, alpha);

			}
		}
		if (saveToFile) {
			imageToTxt(img, textFileName);
		}
	}

	/**
	 * Converts the given image to a text file of POINT commands with the given
	 * filename.
	 * 
	 * @param img      The image to be converted.
	 * @param fileName The name of the new text file.
	 */
	private void imageToTxt(BufferedImage img, String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName);
			// Write initializer line
			writer.write("INIT " + img.getWidth() + " " + img.getHeight() + "\n");
			for (int y = 0; y < img.getHeight(); y++) {
				for (int x = 0; x < img.getWidth(); x++) {

					// Sample the image's rgba values
					int alpha = img.getRGB(x, y) >> 24 & 0xFF;
					int red = img.getRGB(x, y) >> 16 & 0xFF;
					int green = img.getRGB(x, y) >> 8 & 0xFF;
					int blue = img.getRGB(x, y) & 0xFF;
					// Write each pixel in the image as a point to be drawn if the pixel has any
					// information
					if (!(alpha == 0 && red == 0 && green == 0 && blue == 0)) {
						writer.write(
								"POINT " + x + " " + y + " (" + red + "," + green + "," + blue + "," + alpha + ")\n");
					}
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Crops the current canvas so that the remaining pixels have the dimensions
	 * cropWidth x cropHeight. Crop is centered at the given points.
	 * 
	 * @param cropWidth  The width for the canvas to be cropped to.
	 * @param cropHeight The height for the canvas to be cropped to.
	 * @param centerX    The x co-ordinate for the crop to be centered on.
	 * @param centerY    The y co-ordinate for the crop to be centered on.
	 */
	public void crop(int cropWidth, int cropHeight, int centerX, int centerY) {
		// Crop width greater than or equal to the buffer width so only crop the top and
		// bottom
		if (cropWidth >= width) {
			// Do nothing if the crop height is also greater than the buffer height
			if (cropHeight < height) {
				for (int i = 0; i < pixels.length; i++) {
					int x = i % width;
					int y = (i - x) / width;

					// Reset pixels that are outside the bounds of the crop area
					if (centerY - cropHeight / 2 >= 0 && y < centerY - cropHeight / 2) {
						pixels[i] = 0;
					} else if (centerY + cropHeight / 2 < height && y > centerY + cropHeight / 2) {
						pixels[i] = 0;
					}
				}
			}
		} else if (cropHeight >= height) { // Crop height is greater then the buffer height so only crop the left and
											// right
			// Do nothing if the crop width is also greater than the buffer width
			if (cropWidth < width) {
				for (int i = 0; i < pixels.length; i++) {
					int x = i % width;

					// Reset pixels that are outside the bounds of the crop area
					if (centerX - cropWidth / 2 >= 0 && x < centerX - cropWidth / 2) {
						pixels[i] = 0;
					} else if (centerX + cropWidth / 2 < width && x > centerX + cropWidth / 2) {
						pixels[i] = 0;
					}
				}
			}
		} else {
			// Reset all pixels outside the crop area
			for (int i = 0; i < pixels.length; i++) {
				int x = i % width;
				int y = (i - x) / width;

				// Reset pixels that are outside the bounds of the crop area
				if (centerY - cropHeight / 2 >= 0 && y < centerY - cropHeight / 2) {
					pixels[i] = 0;
				} else if (centerY + cropHeight / 2 < height && y > centerY + cropHeight / 2) {
					pixels[i] = 0;
				} else if (centerX - cropWidth / 2 >= 0 && x < centerX - cropWidth / 2) {
					pixels[i] = 0;
				} else if (centerX + cropWidth / 2 < width && x > centerX + cropWidth / 2) {
					pixels[i] = 0;
				}
			}
		}
	}

	/**
	 * Crops the current canvas so that the remaining pixels have the dimensions
	 * cropWidth x cropHeight. Crop is centered at the center of the canvas.
	 * 
	 * @param cropWidth  The width for the canvas to be cropped to.
	 * @param cropHeight The height for the canvas to be cropped to.
	 */
	public void crop(int cropWidth, int cropHeight) {
		this.crop(cropWidth, cropHeight, width / 2, height / 2);
	}

	public int getRed(int xc, int yc) {
		return pixels[xc + width * yc] >> 16 & 0xFF;
	}

	public int getGreen(int xc, int yc) {

		return pixels[xc + width * yc] >> 8 & 0xFF;
	}

	public int getBlue(int xc, int yc) {

		return pixels[xc + width * yc] & 0xFF;
	}

	public int getAlpha(int xc, int yc) {
		return pixels[xc + width * yc] >> 24 & 0xFF;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getPixels() {
		return pixels;
	}
}
