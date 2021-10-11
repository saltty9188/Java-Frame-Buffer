The program uses command line arguments to receive a command file to draw to the frame buffer.
The command files are text files that have the following commands.

INIT |WIDTH| |HEIGHT|
Initialises the framebuffer of WIDTHxHEIGHT pixels. This must be the first command in a file.

POINT |x| |y| |(r,g,b,a)|
Draws a pixel at the position (x,y) with the colour (r,g,b,a).

LINE |x1| |y1| |x2| |y2| |(r,g,b,a)|
Draws a straight line connecting positions (x1, y1) and (x2, y2) with the colour (r,g,b,a)

LINE_FLOAT |x1| |y1| |x2| |y2| |(r,g,b,a)|
Draws a straight line connecting positions (x1, y1) and (x2, y2) with the colour (r,g,b,a) using 
floating point mathematics. This command is slower than using LINE.

OUTLINE_POLYGON |x1| |y1| ... |xn| |yn| |(r,g,b,a)|
Draws the outline of a polygon defined by the positions (x1,y1) through to (xn, yn) with the
colour (r,g,b,a).

FILL_POLYGON |x1| |y1| ... |xn| |yn| |(r,g,b,a)|
Draws a filled polygon defined by the positions (x1,y1) through to (xn, yn) with the
colour (r,g,b,a).

OUTLINE_CIRCLE |x| |y| |r| |(r,g,b,a)|
Draws the outline of a circle centered at (x,y) with radius r with the colour (r,g,b,a).

FILL_CIRCLE |x| |y| |r| |(r,g,b,a)|
Draws a filled circle centered at (x,y) with radius r with the colour (r,g,b,a).

LOAD_PNG |filename.png|
Loads a png image to the framebuffer.

Images can be converted to text files. This is done by including "-save" after the
filepath for the PNG image. For example: 

LOAD_PNG filepath.png -save
This loads the image to the frame buffer as usual, but also saves the loaded image as a series of points
to be drawn within a text file with the same name as the PNG.

A crop function has been implemented which clears the buffer outside the specified area.
It is called using the command:

CROP |WIDTH| |HEIGHT| |xc| |yc|
Crops the buffer outside the area defined by WIDTH and HEIGHT centered at the point (xc,yc).
If xc and yc are not supplied then the crop is centered at the center of the buffer.

SAVE |filename.bmp|
Saves the frame buffer as a bmp file.
