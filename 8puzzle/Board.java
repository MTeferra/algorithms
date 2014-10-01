
/**
 * 
 */

public class Board {
    
    private final int[][] values;
    
   /**
    * construct a board from an N-by-N array of blocks.
    * (where blocks[i][j] = block in row i, column j)
    */
    public Board(int[][] blocks) {
        if (blocks == null) throw new NullPointerException();
        if (blocks.length < 2 || blocks.length >= 128) 
            throw new IllegalArgumentException();
        
        int N = blocks.length;
        this.values = new int[N][N];
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                this.values[row][col] = blocks[row][col];
            }
        }
    }
    
    private int goal(int row, int col) {
        int N = dimension();
        if (row == N-1 && col == N-1) return 0;
        return col + row*N + 1;
    }
    
    /**
     *  board dimension N
     */
    public int dimension() {
        return values.length;
    }
    
    /**
     * number of blocks out of place
     */
    public int hamming() {
        int result = 0;
        int N = dimension();
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (values[row][col] == 0) continue;  // skip blank block
                if (values[row][col] != goal(row, col))  result++;
            }
        }
        return result;
    }
    
    /**
     * sum of Manhattan distances between blocks and goal
     */
    public int manhattan() {
        int result = 0;
        int N = dimension();
        
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (values[row][col] == 0) continue;   // skip blank block
                if (goal(row, col) == values[row][col]) continue;
                    int dx = (values[row][col] - 1) / N;
                    int dy = values[row][col] - 1 - dx * N;
                    int d = Math.abs(row - dx) + Math.abs(col - dy);
                    result += d;
            }
        }       
        return result;          
    }
    
    /**
     * Is this board the goal board?
     */
    public boolean isGoal() {
        return manhattan() == 0;
    }
    
    private static void swap(int[][] a, int row, int col1, int col2) {
        swap(a, row, col1, row, col2);
    }
    
    private static void swap(int[][] a, int row1, int col1, int row2, int col2) {
        int temp      = a[row1][col1];
        a[row1][col1] = a[row2][col2];
        a[row2][col2] = temp;
   }
    
    private static int[][] copy(int[][] a, int N) {
        int[][] b = new int[N][N];
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                b[row][col] = a[row][col];
            }
        }
        return b;
    }
    
    /**
     * A board obtained by exchanging two adjacent blocks in the same row
     */
    public Board twin() {
        int N = dimension();
        int[][] cp = copy(values, N);
        int N1 = N-1;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N1; col++) {
                if (cp[row][col]   == 0) continue;
                if (cp[row][col+1] == 0) continue;
                swap(cp, row, col, col+1); 
                Board c = new Board(cp);
                return c;
            }
        }
        return null;
    }
    
    /**
     * Does this board equal y?
     */
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (this.dimension() != that.dimension())      return false;
        int N = this.dimension();
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
               if (this.values[row][col] != that.values[row][col]) return false;
            }
        }
        return true;
    }
    
    private Board neighbor(int row, int col, int nrow, int ncol) {
        int N = dimension();
        if (nrow < 0 || nrow >= N) return null;
        if (ncol < 0 || ncol >= N) return null;
        
        int[][] cp = copy(values, N);
        swap(cp, row, col, nrow, ncol);
        Board b = new Board(cp);
        return b;
    }
    
    /**
     * All neighboring boards
     */
    public Iterable<Board> neighbors() {
        Queue<Board> q = new Queue<Board>();
        int N = dimension();
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (this.values[row][col] == 0) {
                    Board left  = neighbor(row, col, row, col-1);
                    Board right = neighbor(row, col, row, col+1);
                    Board up    = neighbor(row, col, row-1, col);
                    Board down  = neighbor(row, col, row+1, col);
                    if (left  != null) q.enqueue(left);
                    if (right != null) q.enqueue(right);
                    if (down  != null) q.enqueue(down);
                    if (up    != null) q.enqueue(up);
                    return q;
                }
            }
        }
        return q;
    }
    
    /**
     * String representation of the board (in the output format specified below)
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = dimension();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", values[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }
}
