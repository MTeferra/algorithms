import java.util.NoSuchElementException;
import java.util.Iterator;

/**
 * The <tt>PointSET</tt> class represents a set of points in the unit square.
 * <p>
 * It immplements the following API by using a red-black BST
 * (of SET from algs4.jar)
 * <p>
 * 
 *  @author Michael Teferra
 */

public class PointSET {
    
    private SET<Point2D> points;
    
    /**
     * construct an empty set of points 
     */
    public         PointSET() {
        points = new SET<Point2D>();
    }
    
    /**
     * is the set empty? 
     */
    public boolean isEmpty() {
        return points.isEmpty();
    }
    
    /**
     * number of points in the set
     */
    public int size() {
        return points.size();
    }
    
    /**
     * add the point to the set (if it is not already in the set)
     */
    public void insert(Point2D p) {
        points.add(p);
    }
    
    /**
     * does the set contain point p?
     */
    public boolean contains(Point2D p) {
        return points.contains(p);
    }
    
    /**
     * draw all points to standard draw
     */
    public void draw() {
        if (points.isEmpty()) return;
        
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        for (Point2D pt : this.points)
            pt.draw();
        StdDraw.show(0);
    }
    
    /**
     * all points that are inside the rectangle
     */
    public Iterable<Point2D> range(RectHV rect) {
        SET<Point2D> set = new SET<Point2D>();
        for (Point2D pt : this.points) {
            if (rect.contains(pt)) {
                set.add(pt);
            }
        }
        return set;
    }
    
    /**
     * a nearest neighbor in the set to point p; null if the set is empty
     */
    public Point2D nearest(Point2D p) {
        if (points.isEmpty()) return null;
        Point2D nearestPt = null;
        double dist = Double.POSITIVE_INFINITY;
        
        for (Point2D pt : this.points) {
            double d = pt.distanceSquaredTo(p);
            if (d < dist) {
                dist = d;
                nearestPt = pt;
            }
        }
        
        return nearestPt;
    }
    
    /**
     * unit testing of the methods 
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        PointSET pset = new PointSET();
        
        while (in.hasNextLine()) {
            try {
                double x = in.readDouble();
                double y = in.readDouble();
                Point2D pt = new Point2D(x, y);
                pset.insert(pt);
            } catch (NoSuchElementException e) {
                StdOut.println("");
            }
        }
        
        StdDraw.clear();
        pset.draw();
        StdDraw.show(50);
        
        // test API nearest
        Point2D pt = new Point2D(0.6, 0.9);
        Point2D npt = pset.nearest(pt);
        StdOut.println("Point Nearest to " + pt + " is " + npt);
        
        // test API range
        RectHV rect = new RectHV(0.1, 0.45, 0.45, 0.75);
        Iterable<Point2D> itb = pset.range(rect);
        Iterator<Point2D> it = itb.iterator();
        StdOut.println("Points inside rectangle: " + rect);
        while (it.hasNext()) {
            Point2D p = it.next();
            StdOut.println(p);
        }      
   }
    
 }

