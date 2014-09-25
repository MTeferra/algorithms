
import java.util.Comparator;

/**
 *  The <tt>Point</tt> class is immutable data type for points in the plane.
 *  <p>
 *  Compilation:  javac Point.java
 *  <p>
 *  Execution:
 *  <p>
 *  Dependencies: StdDraw.java
 * 
 *  @author Michael Teferra
 */

public class Point implements Comparable<Point> {

    // compare points by slope
    public final Comparator<Point> SLOPE_ORDER = new BySlope(); 
    
    /**
     * The SLOPE_ORDER comparator should compare points by the slopes they make
     * with the invoking point (x0, y0). 
     * <p>
     * Formally, the point (x1, y1) is less than the point (x2, y2) if and only
     * if the slope (y1 <mo>?</mo> y0) / (x1 ? x0) is less than the slope 
     * (y2 ? y0) / (x2 ? x0). 
     * <p>
     * Treat horizontal, vertical, and degenerate 
     * line segments as in the slopeTo() method. 
     */
    private class BySlope implements Comparator<Point> {    
        public int compare(Point x1, Point x2)    { 
            double s1 = Point.this.slopeTo(x1);
            double s2 = Point.this.slopeTo(x2);
            if      (s1 < s2)   return -1;
            else if (s1 > s2)   return +1;
            else                return  0;
        }
    } 
    
    private final int x;                              // x coordinate
    private final int y;                              // y coordinate

   /**
     * Create the point
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    // plot this point to standard drawing
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    // draw line between this point and that point to standard drawing
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Slope between this point and that point.
     * <p>
     * It is given by the formula: (y1 <mo>-</mo> y0) / (x1 - x0). 
     * <p>
     *  <ul>
     *  <p><li> Treat the slope of a horizontal line segment as positive zero;.
     *  <p><li> Treat the slope of a vertical line segment as positive infinity;
     *  <p><li> treat the slope of a degenerate line segment 
     * (between a point and itself) as negative infinity.
     *  </ul>
     * @param that the other point of the slope.
     */
    public double slopeTo(Point that) {
        if (this.x == that.x && this.y == that.y)           
                                   return Double.NEGATIVE_INFINITY;
        else if (this.x == that.x) return Double.POSITIVE_INFINITY;
        else if (this.y == that.y) return +0.0;
        else                       return (double) (that.y - this.y) 
                                        / (double) (that.x - this.x);
    }

    /**
     * Is this point lexicographically smaller than that one?
     * <p>
     * Comparing y-coordinates and breaking ties by x-coordinates
     * @param that the point to compare to.
     */
    public int compareTo(Point that) {
        if (this.y != that.y) return this.y - that.y;
        else                  return this.x - that.x;
    }

    // return string representation of this point
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    // unit test
    public static void main(String[] args) {
        /* YOUR CODE HERE */
    }
}
