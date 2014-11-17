import java.awt.Color;
import java.util.Iterator;

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
        transposedPic = removeVerticalSeam(seam, transposedPic);
        
        this.pict = transpose(transposedPic);
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
                if (seam[y] != x) newPict.set(i++, y, pict.get(x, y));
            }
        }
        return newPict;
    }
    
    private double[][] computeEnergyMatrix(Picture curPict) {
        int H = curPict.height();
        int W = curPict.width();
        double[][] eMatrix = new double[H][W];
        
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                eMatrix[y][x] = energy(x, y, curPict);
            }
        }
        return eMatrix;
    }
    
    private int[] findVerticalSeam(Picture curPict) {
        int H = curPict.height();
        int W = curPict.width();
        int[] seam = new int[H];
        double[][] eMatrix = computeEnergyMatrix(curPict);
        Queue<Integer> seamQueue = new Queue<Integer>();
        double minWeight = HIGH_ENERGY*H + 1;
 
        // for each pixel in the top row, compute the shortest path to bottom
        for (int x = 0; x < W; x++) {
            Queue<Integer> q = new Queue<Integer>();
            double totalWeight = eMatrix[0][x];
            int minX = x;
            q.enqueue(minX); 
            for (int y = 0; y < H-1; y++) {
                minX = minWeightColumn(minX, y, eMatrix);
                q.enqueue(minX);
                totalWeight += eMatrix[y+1][minX];
            }
            if (totalWeight < minWeight) {
                minWeight = totalWeight;
                seamQueue = q;
            }
        }
        
        int i = 0;
        Iterator<Integer> it = seamQueue.iterator();
        while (it.hasNext()) seam[i++] = it.next();
        
        return seam;
    }
    
    /*
     * Given an x, y location and the entire energy matrix, returns the column 
     * that has the minimum energy below this location. It is either x-1, x, 
     * or x+1;
     */
    private int minWeightColumn(int x, int y, double[][] eMatrix) {
        double w1 = HIGH_ENERGY;
        double w3 = HIGH_ENERGY;
        double w2 = eMatrix[y+1][x];
        
        if (x > 0)                   w1 = eMatrix[y+1][x-1];
        if (x+1 < eMatrix[0].length) w3 = eMatrix[y+1][x+1];        

        int minX = x;
        if (w1 <= w2 && w1 <= w3)      minX = x-1;
        else if (w3 < w1 && w3 < w2)   minX = x+1;
        
        return minX;
    }
    
    private Picture transpose(Picture curPic) {
        Picture transposedPic = new Picture(height(), width());
        
        int H = height();
        int W = width();
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                Color c = pict.get(x, y);
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

