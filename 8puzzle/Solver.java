import java.util.Iterator;

/**
 * The <tt>Solver</tt> class solves the <em>8-Puzzle Problem</em> and its
 * natural generalizations using the A* Algorithm.
 * <p>
 * Define a search node of the game to be a board, the number of moves made to 
 * reach the board, and the previous search node. 
 * <p>
 * <ul>
 * <p><li> Insert the initial search node (the initial board, 0 moves,
 * and a null previous search node) into a priority queue. 
 * 
 * <p><li> Delete from the priority queue the search node with the 
 * minimum priority, 
 * 
 * <p><li> Insert onto the priority queue all neighboring search nodes 
 * (those that can be reached in one move from the dequeued search node). 
 * 
 * <p><li> Repeat this procedure until the search node dequeued corresponds to 
 * a goal board. 
 * 
 * </ul>
 * <p>
 * The success of this approach hinges on the choice of priority 
 * function for a search node. We consider two priority functions:
 * 
 * To solve the puzzle from a given search node on the priority queue, 
 * the total number of moves we need to make (including those already made) 
 * is at least its priority, using either the Hamming or Manhattan priority 
 * function. 
 * <p>
 * (For Hamming priority, this is true because each block that is out of place
 * must move at least once to reach its goal position. 
 * <p>
 * For Manhattan priority, this is true because each block must move its 
 * Manhattan distance from its goal position. Note that we do not count 
 * the blank square when computing the Hamming or Manhattan priorities.) 
 * <p>
 * Consequently, when the goal board is dequeued, we have discovered not only a
 * sequence of moves from the initial board to the goal board, but one that 
 * makes the fewest number of moves.
 * 
 *  @author Michael Teferra
 */
public class Solver {
    
    private final MinPQ<SearchNode> thePQ;
    private final MinPQ<SearchNode> twinPQ;
    private final Board initial;
    
    private class SearchNode implements Comparable<SearchNode> {
        private Board      board;
        private int        moves;
        private SearchNode prev;
        
        public SearchNode(Board bd, int m, SearchNode node) {
            this.board = bd;
            this.moves = m;
            this.prev  = node;
        }
        public int compareTo(SearchNode that) { 
            return this.board.manhattan() - that.board.manhattan();   
        } 
    }
    
    /**
     * find a solution to the initial board (using the A* algorithm)
     */
    public Solver(Board initial) {
        SearchNode sn = new SearchNode(initial, 0, null);
        this.thePQ = new MinPQ<SearchNode>();
        this.thePQ.insert(sn);
        this.initial = initial;    
        
        SearchNode twinSN = new SearchNode(initial.twin(), 0, null);
        this.twinPQ = new MinPQ<SearchNode>();      
        this.twinPQ.insert(twinSN);
        
        solve();
    }
    
    private boolean isVisited(Board current, SearchNode sn) {
        if (sn.prev == null) return false;
        if (current.equals(sn.prev.board)) return true;
        return isVisited(current, sn.prev);
    }
        
    
    private boolean addNeighbors(MinPQ<SearchNode> pq, SearchNode sn) {
        Board bd = sn.board;
        Iterable<Board> itble = bd.neighbors();
        Iterator<Board> it = itble.iterator();
        int count = 0;
        int inserted = 0;
        while (it.hasNext()) {
            Board nextBd = it.next();
            count++;
            if (!isVisited(nextBd, sn)) {
                SearchNode nextSN = new SearchNode(nextBd, sn.moves+1, sn);
                pq.insert(nextSN);
                inserted++;
            }
        }
        return inserted > 0;
    }
    
    /**
     * Is the initial board solvable?
     */
    public boolean isSolvable() {
        if (thePQ.isEmpty()) return false;
        SearchNode sn = thePQ.min();
        if (sn == null) return false;
        return sn.board.isGoal();
    }
    
    private boolean solve() {      
        boolean result = false;
        while (true) {
            if (thePQ.isEmpty() || twinPQ.isEmpty()) break;
            SearchNode sn    = thePQ.delMin();
            SearchNode twinSN = twinPQ.delMin();
            if (sn.board.isGoal()) {
                result = true;
                thePQ.insert(sn);
                break;
            } else if (twinSN.board.isGoal()) {
                twinPQ.insert(twinSN);
                break;
            }
            addNeighbors(thePQ , sn);
            addNeighbors(twinPQ, twinSN);
        }
        
        return result;
    }
    
    /**
     *  Min number of moves to solve initial board; -1 if no solution
     */
    public int moves() {
        if (!isSolvable()) return -1;
        SearchNode sn = thePQ.min();
        return sn.moves;
    }
    
    /**
     * Sequence of boards in a shortest solution; null if no solution
     */
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        Stack<Board> s = new Stack<Board>();
        SearchNode sn = thePQ.min();
        while (sn != null) {
            s.push(sn.board);
            sn = sn.prev;
        }
        return s;
    }
    
    /**
     * solve a slider puzzle (given below)
     */
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
            blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        
        // solve the puzzle
        Solver solver = new Solver(initial);
        
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
