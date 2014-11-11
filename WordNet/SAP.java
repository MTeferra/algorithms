import java.util.Iterator;

/**
 * SAP  is an immutable class that computes the shortest ancestral path between 
 * two vertices in a Digraph. 
 * <p>
 * An <tt>ancestral path</tt>between two vertices v and w in a digraph is a 
 * directed path from v to a common ancestor x,  together with a directed path 
 * from w to the same ancestor x.
 * <p>
 * <tt>Corner Cases.</tt> All methods should throw a 
 * <tt>java.lang.NullPointerException</tt> if any argument is null. All 
 * methods should throw a <tt>java.lang.IndexOutOfBoundsException</tt> if any
 * argument is invalid -- not between 0 and G.V() - 1.
 * <p>
 * <tt>Performance Requirements.</tt> All methods (and the constructor) should
 * take time at most proportional to E + V in the worst case, where E and V are
 * the number of edges and vertices in the digraph, respectively. Your data type
 * should use space proportional to E + V. 
 */
public class SAP {
    
    private final Digraph dg;
    
    private class Ancestor {
        private int dist;
        private int v;
    }

    /**
     * constructor takes a digraph (not necessarily a DAG)
     * @throws <tt>java.lang.NullPointerException</tt> if argument is null
     * @throws <tt>java.lang.IllegalArgumentException</tt> if not a rooted DAG
     */
    public SAP(Digraph G) {
        if (G == null) throw new NullPointerException();
//        if (!isRootedDAG(G)) throw new IllegalArgumentException();
        
        this.dg = new Digraph(G);
    }

    /**
     * length of shortest ancestral path between v and w; -1 if no such path
     * @throws <tt>java.lang.IndexOutOfBoundsException if invalid arguments.
     */
    public int length(int v, int w) {
        if (!isValid(v) || !isValid(w)) throw new IndexOutOfBoundsException();
        
        Ancestor a = commonAncestor(v, w);
        if (a == null) return -1;
        
        return a.dist;
    }

    /**
     * a common ancestor of v and w that participates in a shortest ancestral 
     * path; -1 if no such path
     */
    public int ancestor(int v, int w) {
        if (!isValid(v) || !isValid(w)) throw new IndexOutOfBoundsException();
        
        
        Ancestor a = commonAncestor(v, w);
        if (a == null) return -1;
        
        return a.v;
    }
        

    /**
     * length of shortest ancestral path between any vertex in v and any vertex 
     * in w; -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new NullPointerException();
        if (!isValid(v) || !isValid(w)) throw new IndexOutOfBoundsException();
        
        Ancestor a = commonAncestor(v, w);
        if (a == null) return -1;
        
        return a.dist;
    }
         
    /**
     * a common ancestor that participates in shortest ancestral path; -1 if no 
     * such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new NullPointerException();
        if (!isValid(v) || !isValid(w)) throw new IndexOutOfBoundsException();
        
        Ancestor a = commonAncestor(v, w);
        if (a == null) return -1;
        
        return a.v;
    }
   
    /**
     * returns the common ancestor for v and w, with the shortest ancestral 
     * path.(null otherwise)
     */
    private Ancestor commonAncestor(int v, int w) {
        BreadthFirstDirectedPaths pv = new BreadthFirstDirectedPaths(dg, v);
        BreadthFirstDirectedPaths pw = new BreadthFirstDirectedPaths(dg, w);
        
        int minLength = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < dg.V(); i++) {
            if (pv.hasPathTo(i) && pw.hasPathTo(i)) {
                int dist = pv.distTo(i) + pw.distTo(i);
                if (dist < minLength) {
                    minLength = dist;
                    ancestor = i;
                }
            }
        }
        if (minLength == Integer.MAX_VALUE) return null;
        
        Ancestor a = new Ancestor();
        a.dist = minLength;
        a.v    = ancestor;
        
        return a;
    }
 
    /**
     * returns the common ancestor for v and w, with the shortest ancestral 
     * path.(null otherwise)
     */
    private Ancestor commonAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths pv = new BreadthFirstDirectedPaths(dg, v);
        BreadthFirstDirectedPaths pw = new BreadthFirstDirectedPaths(dg, w);
        
        int minLength = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < dg.V(); i++) {
            if (pv.hasPathTo(i) && pw.hasPathTo(i)) {
                int dist = pv.distTo(i) + pw.distTo(i);
                if (dist < minLength) {
                    minLength = dist;
                    ancestor = i;
                }
            }
        }
        if (minLength == Integer.MAX_VALUE) return null;
        
        Ancestor a = new Ancestor();
        a.dist = minLength;
        a.v    = ancestor;
        
        return a;
    }
  
    /**
     * A Digraph has a topological order iff no directed cycle.
     */
    private boolean isRootedDAG(Digraph G) {
        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle()) return false;
        
        Topological t = new Topological(G);
        return t.hasOrder();
    }   
   
    private boolean isValid(int v) {
        if (v < 0 || v >= dg.V()) return false;
        return true;
    }
    
    private boolean isValid(Iterable<Integer> list) {
        Iterator<Integer> it = list.iterator();
        if (!it.hasNext()) return false;
        
        while (it.hasNext()) {
            int v = it.next();
            if (!isValid(v)) return false;
        }
        
        return true;
    }

    /**
     * <tt>Unit Test.</tt> takes the name of a digraph input file as as a 
     * command-line argument, constructs the digraph, reads in vertex pairs 
     * from standard input, and prints out the length of the shortest ancestral
     * path between the two vertices and a common ancestor that participates in
     * that path: 
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
