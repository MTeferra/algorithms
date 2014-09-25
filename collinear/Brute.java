import java.util.Comparator;
import java.util.Arrays;

/**
 *  The <tt>Brute</tt> class is a <em>brute-force algorithm</em> for checking 
 * collinearity of points.
 *  <p>
 *  Compilation:  javac Brute.java
 *  <p>
 *  Execution: java Brute <inputfile>
 * <p>
 *  Dependencies: Point.java
 * 
 *  @author Michael Teferra
 */

public class Brute {
    
    private static void collinear(Point[] pts) {
        
        Arrays.sort(pts);
        int N = pts.length;
        for (int i = 0; i < N; i++) {
            for (int j = i+1; j < N; j++) {
                for (int k = j+1; k < N; k++) {
                    for (int l = k+1; l < N; l++) {
                        // collinear - if the comparison is zero
                        Comparator<Point> c1 = pts[i].SLOPE_ORDER;
                        Comparator<Point> c2 = pts[j].SLOPE_ORDER;
                        if (c1.compare(pts[j], pts[k]) == 0 
                          && c2.compare(pts[k], pts[l]) == 0) {
                            StdOut.println(pts[i] + " -> " + pts[j] + " -> "
                                         + pts[k] + " -> " + pts[l]);
                            pts[i].draw();
                            pts[l].draw();
                            pts[i].drawTo(pts[l]);
                        }
                     }
                }
            }
        }
    }
    
   /**
     * Reads in the number of points from a file. Then reads
     * the x-y coordinates of each point.examines 4 points at a time and checks 
     * whether they all lie on the same line segment, printing out any such line
     * segments to standard output and drawing them using standard drawing. 
     * <p>
     * To check whether the 4 points p, q, r, and s are collinear, check whether
     * the slopes between p and q, between p and r, and between p and s are 
     * all equal. 
     */
    
    public static void main(String[] args) {
              
        // rescale coordinates and turn on animation mode
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        //StdDraw.setPenRadius(.02);
        StdDraw.show(0);
        
        // read the number of points from the file
        In in = new In(args[0]);
        int numPoints = in.readInt();
        
        // read the coordinates of each point
        Point[] pts = new Point[numPoints];
        for (int i = 0; i < numPoints; i++) {
            int x = in.readInt();
            int y = in.readInt();
            pts[i] = new Point(x, y);
        }
        
        collinear(pts);
        
        // display to screen all at once
        StdDraw.show(0);
   }
}
