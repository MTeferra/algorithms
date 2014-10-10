import java.util.NoSuchElementException;
import java.util.Iterator;

/**
 * The <tt>KdTree</tt> class is a a 2d-tree implementation for 2dPoints..
 * <p>
 * The idea is to build a BST with points in the nodes, using the x- and 
 * y-coordinates of the points as keys in strictly alternating sequence.
 * <p>
 * Each node corresponds to an axis-aligned rectangle in the unit square, 
 * which encloses all of the points in its subtree. The root corresponds to the
 * unit square; the left and right children of the root corresponds to the two
 * rectangles split by the x-coordinate of the point at the root; and so forth.
 * <p>
 * 
 *  @author Michael Teferra
 */
public class KdTree {
    
    private static int K = 2;    // 2d Tree by default    
    
    private Node root;           // root of the KdTree
    private int  N;              // size of the subtree.
    
    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle for this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        
        public Node(Point2D pt) {
            this.p    = pt;
            this.lb   = null;
            this.rt   = null;
            this.rect = null;
        }
    }
    
    /**
     * construct an empty KdTree
     */
    public KdTree() {
        root = null;
        N = 0;
    }
    
    /**
     * Is the KdTree empty?
     */
    public boolean isEmpty() {
        return size() == 0;
    }
    
    /**
     * size of the KdTree
     */
    public int size() {
        return N;
    }
    
    /**
     * add a point to the KdTree
     */
    public void insert(Point2D p) {
        root = insert(root, p, 0);
    }
    
    /**
     * does the KdTree contain point p?
     */
    public boolean contains(Point2D p) {
        return get(root, p, 0) != null;
    }
   
    /**
     * Draw all points to standard draw
     */
    public void draw() {
        draw(root, null, 0);
    }
    
    /**
     * All points that are inside the rectangle.
     * To find all points contained in a given query rectangle, start at the 
     * root and recursively search for points in both subtrees using the 
     * following pruning rule: if the query rectangle does not intersect the
     * rectangle corresponding to a node, there is no need to explore that node
     * (or its subtrees). A subtree is searched only if it might contain a point
     * contained in the query rectangle.
     */
    public Iterable<Point2D> range(RectHV rect) {
        SET<Point2D> s = new SET<Point2D>();
        range(rect, root, 0, s);
        return s;
    }
    
    /**
     * a nearest neighbor in the KdTree to point p; null if the tree is empty 
     */
    public Point2D nearest(Point2D p) {
        if (root == null) return null;
        Node nnd = nearest(root, p);
        return nnd.p;
    }
    
    /**
     * add a point to the KdTree at the specified depth.
     * @param x the parent of the node.
     * @param pt the point to be added.
     * @parem depth the depth in the KdTree.
     */
    private Node insert(Node x, Point2D pt, int depth) {
        if (x == null) { 
            Node n = new Node(pt);
            n.rect = new RectHV(0., 0., 1., 1.);
            N++;
            return n;
        }
        if (pt.equals(x.p)) {
            return x;
        }
        
        double cmp = compare(pt, x.p, depth);
        if (cmp < 0.) { 
            x.lb = insert(x.lb, pt, depth+1);
            addRect(x.lb, x, depth, cmp);
        } else {
            x.rt = insert(x.rt, pt, depth+1);
            addRect(x.rt, x, depth, cmp);
        }
        return x;
    }
    
    private void addRect(Node x, Node xp, int depth, double cmp) {
        if (depth % K == 0)  splitByYAxis(x, xp, cmp);
        else                 splitByXAxis(x, xp, cmp);
    }
    
    private void splitByYAxis(Node x, Node xp, double cmp) {
        if (cmp < 0.) x.rect = new RectHV(xp.rect.xmin(), xp.rect.ymin(),
                                          xp.p.x()      , xp.rect.ymax());
        else          x.rect = new RectHV(xp.p.x()      , xp.rect.ymin(),
                                          xp.rect.xmax(), xp.rect.ymax());
    }
    
    private void splitByXAxis(Node x, Node xp, double cmp) {
        if (cmp < 0.) x.rect = new RectHV(xp.rect.xmin(), xp.rect.ymin(),
                                          xp.rect.xmax(), xp.p.y());
        else          x.rect = new RectHV(xp.rect.xmin(), xp.p.y(),
                                          xp.rect.xmax(), xp.rect.ymax());
    }
 
    private Point2D get(Node x, Point2D pt, int depth) {
        if (x == null) return null;
        if (x.p.equals(pt)) return x.p;
        double cmp = compare(pt, x.p, depth);
        if      (cmp < 0.) return get(x.lb, pt, depth+1);
        else               return get(x.rt, pt, depth+1);
    }
    
    private double compare(Point2D p1, Point2D p2, int depth) {
        if (depth % K == 0) return compareX(p1, p2);
        else                return compareY(p1, p2);
    }
    
    private double compareX(Point2D p1, Point2D p2) {
        if (p1.x() < p2.x()) return -1;
        if (p1.x() > p2.x()) return +1;
        return 0;
    }
    
    private double compareY(Point2D p1, Point2D p2) {
        if (p1.y() < p2.y()) return -1;
        if (p1.y() > p2.y()) return +1;
        return 0;
    }
    
    private void draw(Node x, Node xp, int depth) {
        if (x == null) return;
        
        // draw the point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        x.p.draw();
        
        double cmp = 0.;
        Point2D p2 = null;
        if (xp != null)  {
            cmp = compare(x.p, xp.p, depth-1);
            p2 = xp.p;
        } 
        if (depth % K == 0) drawVLine(x.p, p2, cmp < 0.);
        else                drawHLine(x.p, p2, cmp < 0.);
        draw(x.lb, x, depth+1);
        draw(x.rt, x, depth+1);
    }
    
    private void drawVLine(Point2D p, Point2D pp, boolean isLess) {
        double y1 = 0.;
        double y2 = 1.;
        if (pp != null) {
            y1 = pp.y();
            if (isLess)  y2 = 0.;
        }
        Point2D p1 = new Point2D(p.x(), y1);
        Point2D p2 = new Point2D(p.x(), y2);
        
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius();
        p1.drawTo(p2);
    }
    
    private void drawHLine(Point2D p, Point2D pp, boolean isLess) {
        double x1 = 0.;
        double x2 = 1.;
        if (pp != null) {
            x1 = pp.x();
            if (isLess)  x2 = 0.;
        }
        Point2D p1 = new Point2D(x1, p.y());
        Point2D p2 = new Point2D(x2, p.y());
       
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius();
        p1.drawTo(p2);
    }
    
    private Node nearest(Node x, Point2D pt) {
        Node bl = x;
        Node br = x;
        if (x.lb != null) {
                bl = nearest(x.lb, pt);
        }
        if (x.rt != null) {
                br = nearest(x.rt, pt);
        }
        double dl = bl.p.distanceSquaredTo(pt);
        double dr = br.p.distanceSquaredTo(pt);
        if (dl < dr) return bl;
        return br;
    }
    
    private void range(RectHV rect, Node x, int depth, SET<Point2D> s) {
        if (x == null) return;
        if (rect.contains(x.p))  {
            s.add(x.p);
            range(rect, x.lb, depth+1, s);
            range(rect, x.rt, depth+1, s);
            return;
        }
                
        if (depth % K == 0) {
            if (x.p.x() > rect.xmax())      range(rect, x.lb, depth+1, s);
            else if (x.p.x() < rect.xmin()) range(rect, x.rt, depth+1, s);
            else { // intersects
                range(rect, x.lb, depth+1, s);
                range(rect, x.rt, depth+1, s);
            }
        } else {
            if (x.p.y() > rect.ymax())      range(rect, x.lb, depth+1, s);
            else if (x.p.y() < rect.ymin()) range(rect, x.rt, depth+1, s);
            else {  // intersects
                range(rect, x.lb, depth+1, s);
                range(rect, x.rt, depth+1, s);
            }
        }
                    
    }
    
    /**
     * unit testing of the methods (optional) 
     */
    public static void main(String[] args) {
       In in = new In(args[0]);
        KdTree kdtree = new KdTree();
        
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D pt = new Point2D(x, y);
            kdtree.insert(pt);
            if (!kdtree.contains(pt)) {
                throw new NoSuchElementException();
            }
        }
        
        StdDraw.clear();
        kdtree.draw();
        StdDraw.show(50);
        
        StdOut.println("size = " + kdtree.size());        
        Point2D p1 = new Point2D(0., 0.);
        Point2D np = kdtree.nearest(p1);
        StdOut.println("nearest point " + np + " to " + p1);
        
        RectHV r1 = new RectHV(0.1, 0.1, 0.5, 0.6);
        Iterable<Point2D> itb = kdtree.range(r1);
        Iterator<Point2D> it = itb.iterator();
        StdOut.println("range API " + r1);
        while (it.hasNext()) {
            Point2D p = it.next();
            StdOut.println("point in range: " + p);
        }
    }
}

