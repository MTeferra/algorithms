import java.awt.Color;

/**
 * <tt>SeamCarver</tt> class resizes a <tt>W-by-H</tt> image using the seam 
 * carving technique.
 * <p>
 * Seam-carving is a content-aware image resizing technique where the image is 
 * reduced in size by one pixel of height (or width) at a time. 
 * A <tt>vertical seam</tt> in an image is a path of pixels connected from the 
 * top to the bottom with one pixel in each row. A <tt>horizontal seam</tt> is 
 * a path of pixels connected from the left to the right with one pixel in each
 * column. Unlike standard content-agnostic resizing techniques (e.g. cropping 
 * and scaling), the most interesting features (aspect ratio, set of objects 
 * present, etc.) of the image are preserved. 
 * <p>
 * Finding and removing a seam involves three parts and a tiny bit of notation: 
 * <p>
 * <ul>
 * <p><li><tt>Notation. </tt>In image processing, pixel (x, y) refers to the 
 * pixel in column x and row y, with pixel (0, 0) at the upper left corner and
 * pixel (W - 1, H - 1) at the bottom right corner. <tt>Warning: </tt> this is
 * the opposite of the standard mathematical notation used in linear algebra 
 * where <tt>(i,j)</tt> refers to row <tt>i</tt> and column <tt>j</tt> and
 * with Cartesian Coordinates where (0,0) refers to the lower bottom corner.
 * <p>
 * We also assume that the color of a pixel is represented in RGB space, using
 * three integers between 0 and 255. This is consistent with <tt>java.awt.Color
 * </tt>.
 * <p><li><tt>Energy Calculation. </tt>The first step is to calculate the 
 * <tt>energy</tt> of each pixel, which is a measure of the importance of each 
 * pixel the higher the energy, the less likely that the pixel will be included
 * as part of a seam (as we'll see in the next step). 
 * A <tt>dual gradient energy function</tt>, as described below is implemented.
 * The energy is high (white) for pixels in the image where there is a 
 * rapid color gradient. This seam-carving technique avoids removing such
 * high-energy pixels.
 * <p><li><tt>Seam identiication. </tt>The next step is to find a vertical seam
 * of minimum total energy. This is similar to the classic shortest path problem
 * in an edge-weighted digraph except for the following:
 * <ul>
 * <p><li>The weights are on the vertices instead of the edges. 
 * <p><li>We want to find the shortest path from any of the W pixels in the top 
 * row to any of the W pixels in the bottom row. 
 * <p><li>The digraph is acyclic, where there is a downward edge from pixel 
 * (x, y) to pixels (x - 1, y + 1), (x, y + 1), and (x + 1, y + 1), 
 * assuming that the coordinates are in the prescribed range. 
 * </ul>
 * <p><li><tt>Seam removal. </tt> The final step is to remove from the image 
 * all of the pixels along the seam.
 * <p><li><tt>Performance Requirements. </tt> The <tt>width(), height()</tt> and
 * <tt>energy()</tt> methods should take constant time in the worst case. All 
 * other methods should run in time proportional to W H in the worst case. For
 * faster performance, do not construct explicit <tt>DirectedEdge</tt> and 
 * EdgeWeightedDirectedGraph objects.
 * </ul>
 * <p>
 *
 *  @author Michael Teferra

 */

public class SeamCarver {
    
    private static final double HIGH_ENERGY = 3.0*255*255;
    private Picture pict;
    private double[][] eMatrix;
    
    /**
     * create a seam carver object based on the given picture
     */
    public SeamCarver(Picture picture) {
        this.pict = picture;
    }
    
    /**
     * current picture
     */
    public Picture picture()  {
        return this.pict;
    }
    
    /**
     * width of current picture
     */
    public int width() {
        return pict.width();
    }
    
    /**
     * height of current picture
     */
    public int height() {
        return pict.height();
    }
    
    /**
     * Energy of pixel at column x and row y.
     * <tt>Dual Gradient Energy</tt>  computation is used. The energy of 
     * pixel(x,y) is <tt>Dx^2(x,y) + Dy^2(x,y)</tt>, where the square of the
     * x-gradient <tt>Dx^2 = Rx(x,y)^2 + Gx(x,y)^2 + Rx(x,y)^2</tt> and where 
     * the central differences <tt>Rx(x,y),Gx(x,y) and Bx(x,y)</tt> are the 
     * absolute value in the differences in red, blue, and green pixel
     * components between pixel</tt>(x+1,y),/tt> and pixel<tt>(x-1,y)</tt>.
     * The square of the <tt>y-gradient</tt> is defined in an analogous manner.
     * We define the energy of pixels at the border of the image to be
     * <tt>255^2 + 255^2 + 255^2 = 195075</tt>.
     * <p>
     * @throws <tt>java.lang.IndexOutOfBoundsException</tt> if either argument
     * is outside the range of the picture.
     */
    public  double energy(int x, int y) {
        if (!isValid(x, y)) throw new IndexOutOfBoundsException();
        
        return energy(x, y, this.pict);
    }
    
    /**
     * sequence of indices for horizontal seam
     * @return An array of length W such an entry y is the row number of the
     * pixel to be removed from column y of the image.
     */
    public int[] findHorizontalSeam() {
        Picture transposedPic = transpose(this.pict);
        return findVerticalSeam(transposedPic);
    }
    
    /**
     * sequence of indices for vertical seam
     * @return An array of length H such an entry x is the column number of the
     * pixel to be removed from row x of the image.
     */
    public int[] findVerticalSeam() {
        
        return findVerticalSeam(new Picture(this.pict));
    }
    
    /**
     * remove horizontal seam from current picture
     * @throws <tt>java.lang.NullPointerException</tt> if argument is null.
     * @throws <tt>java.lang.IllegalArgumentException</tt> if array length
     * is wrong.
     */
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new NullPointerException();
        if (seam.length != width()) throw new IllegalArgumentException();
       
        Picture transposedPic = transpose(this.pict);
        this.pict = transpose(removeVerticalSeam(seam, transposedPic));
    }
    
    /**
     * remove vertical seam from current picture
     * @throws <tt>java.lang.NullPointerException</tt> if argument is null.
     * @throws <tt>java.lang.IllegalArgumentException</tt> if array length
     * is wrong.
     */
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new NullPointerException();
        if (seam.length != height()) throw new IllegalArgumentException();
        
        this.pict = removeVerticalSeam(seam, this.pict);
    }
    
    private double energy(int x, int y, Picture curPict) {
        if (isBorderPixel(x, y, curPict)) return HIGH_ENERGY;
        
        
        Color left  = curPict.get(x-1, y);
        Color right = curPict.get(x+1, y);
        Color up    = curPict.get(x, y-1);
        Color down  = curPict.get(x, y+1);
        
        int dxR = right.getRed()   - left.getRed();
        int dxG = right.getGreen() - left.getGreen();
        int dxB = right.getBlue()  - left.getBlue();
        double dx2 = dxR*dxR + dxG*dxG + dxB*dxB;
         
        int dyR = up.getRed()   - down.getRed();
        int dyG = up.getGreen() - down.getGreen();
        int dyB = up.getBlue()  - down.getBlue();
        double dy2 = dyR*dyR + dyG*dyG + dyB*dyB;
        
        return dx2 + dy2;
    }
    
    private Picture removeVerticalSeam(int[] seam, Picture curPict) {
        int W = curPict.width();
        int H = curPict.height();
        Picture newPict = new Picture(W-1, H);
        
        for (int y = 0; y < H; y++) {
            int i = 0;
            for (int x = 0; x < W; x++) {
                if (seam[y] != x) newPict.set(i++, y, curPict.get(x, y));
            }
        }
        return newPict;
    }
    
    private void computeEnergyMatrix(Picture curPict) {
        int H = curPict.height();
        int W = curPict.width();
        eMatrix = new double[W][H];
        
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                eMatrix[x][y] = energy(x, y, curPict);
            }
        }
    }
    
    private void init(int[][] edgTo, double[][]distTo, int H, int W) {
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                edgTo[x][y] = 0;
                if (y == 0) distTo[x][y] = this.HIGH_ENERGY;
                else        distTo[x][y] = Double.POSITIVE_INFINITY;
            }
        }
    }
    
    private void relax(int x0, int y0, int x1, int y1, int n,
                       int[][] edgeTo, double[][] distTo) {
        if (distTo[x1][y1] > distTo[x0][y0] + eMatrix[x1][y1]) {
            distTo[x1][y1] = distTo[x0][y0] + eMatrix[x1][y1];
            edgeTo[x1][y1] = n;
        }
    }
    
    private int[] findVerticalSeam(Picture curPict) {
        int H = curPict.height();
        int W = curPict.width();
        int[] seam = new int[H];
        computeEnergyMatrix(curPict);
        int[][] edgeTo     = new int[W][H];
        double[][] distTo  = new double[W][H];
        
        init(edgeTo, distTo, H, W);
  
        // go through the topological sorted graph and compute total energy
        for (int y = 0; y < H-1; y++) {
            int n = y*W;
            for (int x = 0; x < W; x++) {
                int nn = n + x;
                int y1 = y+1;
                int x1 = x-1;
                int x2 = x+1;
                if (x > 0) relax(x, y, x1, y1, nn, edgeTo, distTo);
                relax(x, y, x, y1, nn, edgeTo, distTo);
                if (x2 < W) relax(x, y, x2, y1, nn, edgeTo, distTo);
            }
        }
        
        // now find the minimum weight location
        int y = H-1;
        int minX = 0;
        for (int x = 1; x < W; x++) {
            if (distTo[minX][y] > distTo[x][y]) minX = x;
        }
        while (y >= 0) {
            seam[y] = minX;
            minX = edgeTo[minX][y];
            minX = minX % W;
            y--;
        }
                
        return seam;
    }
     
    private Picture transpose(Picture curPict) {
        Picture transposedPic = new Picture(curPict.height(), curPict.width());
        
        int H = curPict.height();
        int W = curPict.width();
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                Color c = curPict.get(x, y);
                transposedPic.set(y, x, c);
            }
        }
        
        return transposedPic;
    }
    
    private boolean isBorderPixel(int x, int y, Picture curPict) {
        if (x == 0 || y == 0 || x == (curPict.width() - 1) 
                || y == (curPict.height() - 1)) return true; 
        
        return false;
    }
    
    private boolean isValid(int x, int y) {
        if (x < 0 || x >= pict.width())  return false;
        if (y < 0 || y >= pict.height()) return false;
        return true;
    }
}

