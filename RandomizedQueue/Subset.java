/**
 *  The <tt>Subset</tt> is a client program 
 *  that takes a command-line integer k; reads in a sequence of N strings from 
 * standard input using StdIn.readString(); and prints out exactly k of them, 
 * uniformly at random. 
 * 
 * Requirement; Each item from the sequence can be printed out at most once. 
 *              The running time of Subset must be linear in the size of the 
 *              input. Uses only a constant amount of memory plus either
 *              one Deque or RandomizedQueue object of maximum size at most N, 
 *              where N is the number of strings on standard input. 
 *              (For an extra challenge, use only one Deque or RandomizedQueue
 *               object of maximum size at most k.) 
 *  <p>
 *
 *  @author Michael Teferra
 */
public class Subset {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            rq.enqueue(item);
        }
        for (int i = 0; i < k; i++) {
            StdOut.println(rq.dequeue());
        }
    }
}
