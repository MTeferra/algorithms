import java.util.Arrays;
import java.util.Comparator;

/**
 * The <tt>Fast</tt> class is a <em>faster sorting-based algorithm</em> for
 * checking collinearity of points.
 * <p>
 * Given a point p, the following method determines whether p participates 
 * in a set of 4 or more collinear points.
 * <p>
 *  <ul>
 *  <p><li> Think of p as the origin.
 *  <p><li> For each other point q, determine the slope it makes with p.
 *  <p><li> Sort the points according to the slopes they makes with p. 
 *  <p><li> Check if any 3 (or more) adjacent points in the sorted order 
 * have equal slopes with respect to p. If so, these points, together with p, 
 * are collinear. 
 *  </ul>
 * <p>
 * Applying this method for each of the N points in turn yields an efficient 
 * algorithm to the problem. The algorithm solves the problem because points 
 * that have equal slopes with respect to p are collinear, and sorting brings 
 * such points together. The algorithm is fast because the bottleneck operation 
 * is sorting. 
 *  <p>
 *  Compilation:  javac Fast.java
 * <p>
 *  Execution: java Fast <inputfile>
 * <p>
 *  Dependencies: Point.java
 * 
 *  @author Michael Teferra
 */


public class Fast {
    
    private void collinear(Point[] pts) {
        
        Arrays.sort(pts);       // put the points in natural order
        
        int N = pts.length;
        String[] printedItemList = new String[0];
        Point[] sortedPts = new Point[N];
        for (int i = 0; i < N; i++) {
            
            for (int j = i; j < N; j++) 
                sortedPts[j] = pts[j]; 
            
            Arrays.sort(sortedPts, i+1, N, pts[i].SLOPE_ORDER);
            
            for (int j = i+1;  j < N; j++) {
                String[] colPts = collinearPoints(sortedPts, i, j);
                if (colPts != null  && colPts.length > 3) {
                    String str = collinearPtsString(sortedPts, colPts);
                    if (!isPrinted(sortedPts, colPts, printedItemList)) {
                        StdOut.println(str);
                        String[] temp = new String[printedItemList.length+1];
                        for (int k = 0; k < printedItemList.length; k++) 
                            temp[k] = new String(printedItemList[k]);
                        temp[printedItemList.length] = new String(str);
                        printedItemList = temp;
                        draw(sortedPts, colPts);
                    }
                }
            }
        }
    }
    
    // returns list of collinear points from the given set of points that
    // have already been ordered by SLOPE. Stops when the slope does not match.
    // those with the slope of pts[i] -> pts[j].
    private String[] collinearPoints(Point[] pts, int i, int j) {
        
        String result = null;
        Comparator<Point> c1 = pts[i].SLOPE_ORDER; 
        int N = pts.length;
        for (int k = j+1; k < N; k++) {
            if (c1.compare(pts[j], pts[k]) == 0) {
                if (result == null) result = new String(i + ":" + j);
                result = result + ":" + k;
            }
        }
        if (result == null) return null;
        return result.split(new String(":"));
    }
    
    // convert the given collinear points into a formatted-printable string.
    private String collinearPtsString(Point[] pts, String[] colPts) {

        int ndx = Integer.parseInt(colPts[0]);
        String str = new String(pts[ndx].toString());
        for (int i = 1; i < colPts.length; i++) {
           str += " -> ";
           ndx = Integer.parseInt(colPts[i]);
           str += pts[ndx].toString();
        }
        
        return str;
    }
    
    // draw the collinear points
    private void draw(Point[] pts, String[] colPts) {
        Point[] drawPts = new Point[colPts.length];
        for (int j = 0; j < colPts.length; j++) {
            int ndx = Integer.parseInt(colPts[j]);
            drawPts[j] = pts[ndx];
        }
        Arrays.sort(drawPts, 0, colPts.length-1);
        drawPts[0].drawTo(drawPts[drawPts.length-1]);
    }
    
    // check if this set of collinear points is a subset of a previous one that
    // has already been printed.
    private static boolean isPrinted(Point[] pts, String[] colPts, 
                                     String[] printedItemList) {
        if (printedItemList.length == 0) return false;
        for (int i = 0; i < printedItemList.length; i++) {
            boolean bIsPrinted = true;
            for (int j = 0; j < colPts.length; j++) {
                int ndx = Integer.parseInt(colPts[j]);
                if (!printedItemList[i].contains(pts[ndx].toString())) {
                    bIsPrinted = false;
                    break;
                }
            }
            if (bIsPrinted) return true;
        }
        return false;
    }
    
  /**
     * Reads in the number of points from a file. Then reads
     * the x-y coordinates of each point.examines 4 points at a time and checks 
     * whether they all lie on the same line segment, printing out any such line
     * segments to standard output and drawing them using standard drawing. 
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
            pts[i].draw();
        }
        
        Fast fst = new Fast();
        fst.collinear(pts);
        
        // display to screen all at once
        StdDraw.show(0);
    }
}
