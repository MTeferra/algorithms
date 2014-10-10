/**
 * The <tt>RectHV</tt> class is an implementation for axis-alaigned rectangles.
 * <p>
 * 
 *  @author Michael Teferra
 */

public class RectHV {
    private final double xmin, ymin;   // minimum x- and y-coordinates
    private final double xmax, ymax;   // maximum x- and y-coordinates
   
    /**
     * construct the rectangle [xmin, xmax] x [ymin, ymax].
     * @throws a java.lang.IllegalArgumentException if <tt>xmin &gt; xmax</tt> 
     * or <tt>ymin &gt; ymax</tt>
     * @param xmin x-coordinate of lower-left corner of the rectangle
     * @param ymin y-coorcinate of lower-left corner of the rectangle
     * @param xmax x-coordinate of upper-right corner of the rectangle
     * @param ymax y-coordinate of upper-right corner of the rectangle
     */
    public    RectHV(double xmin, double ymin,
                     double xmax, double ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }
    
    /**
     * accessor methods for corners of rectangle. 
     */
    public double xmin() { return xmin; }
    public double ymin() { return ymin; }
    public double xmax() { return xmax; }
    public double ymax() { return ymax; }
    
    /**
     * does this rectangle contain the point p (either inside or on boundary)? 
     */
    public boolean contains(Point2D p) {
         return (p.x() >= xmin) && (p.x() <= xmax)
            && (p.y() >= ymin) && (p.y() <= ymax);
    }
    
    /**
     * does this rectangle intersect that rectangle (at one or more points)? 
     */
    public boolean intersects(RectHV that) {
        return this.xmax >= that.xmin && this.ymax >= that.ymin
            && that.xmax >= this.xmin && that.ymax >= this.ymin;
    }
    
    /**
     * Euclidean distance from point p to closest point in rectangle
     */
    public double distanceTo(Point2D p) {
        return Math.sqrt(this.distanceSquaredTo(p));
    }

    /**
     * distance squared from p to closest point on this axis-aligned rectangle
     */
    public double distanceSquaredTo(Point2D p) {
        double dx = 0.0, dy = 0.0;
        if      (p.x() < xmin) dx = p.x() - xmin;
        else if (p.x() > xmax) dx = p.x() - xmax;
        if      (p.y() < ymin) dy = p.y() - ymin;
        else if (p.y() > ymax) dy = p.y() - ymax;
        return dx*dx + dy*dy;
    }
    
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        RectHV that = (RectHV) y;
        if (this.xmin != that.xmin) return false;
        if (this.ymin != that.ymin) return false;
        if (this.xmax != that.xmax) return false;
        if (this.ymax != that.ymax) return false;
        return true;
    }

    /**
     * return a string representation of this axis-aligned rectangle
     */
    public String toString() {
        return "[" + xmin + ", " + ymin + "] x [" + xmax + ", " + ymax + "]";
    }

   // draw this axis-aligned rectangle
    public void draw() {
        StdDraw.line(xmin, ymin, xmax, ymin);
        StdDraw.line(xmax, ymin, xmax, ymax);
        StdDraw.line(xmax, ymax, xmin, ymax);
        StdDraw.line(xmin, ymax, xmin, ymin);
    }

}
