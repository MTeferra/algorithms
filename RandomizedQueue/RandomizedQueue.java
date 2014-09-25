/**
 *  The <tt>RandomizedQueue</tt> class is similar to a stack or queue, except 
 * that the item removed is chosen uniformly at random from items in the data 
 * structure.
 *  <p>
 * Requirement; The randomized queue implementation must support each randomize
 *               queue operation (besides creating an iterator) in constant 
 *               amortized time and use space proportional to the number of 
 *               items currently in the queue. That is, any sequence of M 
 *               randomized queue operations (starting from an empty queue) 
 *               should take at most cM steps in the worst case, for some 
 *               constant c. Additionally, the iterator implementation must
 *               support construction in time linear to the number of items and
 *               it must support the operations next() and hasNext() in constant 
 *               worst-case time; you may use a linear amount of extra memory 
 *               per iterator. The order of two or more iterators to the same 
 *               randomized queue should be mutually independent; each iterator
 *               must maintain its own random order.
 *  <p>
 *
 *  @author Michael Teferra
 */

import java.util.NoSuchElementException;
import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {
    
    private Item[] q;        // array of items in queue.
    private int    N;        // number of elements in the queue.
    
    /**
     * Construct an empty randomized queue,
     */
    public RandomizedQueue() {
        q     = (Item[]) new Object[2];
        N     = 0;
    }
    
    /**
     * Is the deque empty ?
     */
    public boolean isEmpty() {
        return N == 0;
    }
    
    /**
     * Return the number of items on the queue
     */
    public int size() {
        return N;
    }
 
    // rewize the queue
    private void resize(int capacity) {
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < N; i++)  {
            temp[i] = q[i];
        }
        q = temp;
    }
       
    /**
     * Add the item,
     * @throws a NullPointerException if <tt>item is null</tt>
     * @param item the item to be added
     */
    public void enqueue(Item item) {
        if (item == null) throw new NullPointerException();
        if (N == q.length) resize(2*q.length);
        q[N++]   = item;
    }
     
    // swap order of two queue elements.
    private void swap(int i, int j) {
        Item temp = q[i];
        q[i] = q[j];
        q[j] = temp;
    }
   
    /**
     * Delete and return a random item,
     * @throws a java.util.NoSuchElementException if <tt>queue is empty</tt>
     */
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is Empty");
        if (N > 1) {
            int i = StdRandom.uniform(N); 
            swap(i, N-1);
         }
        Item item = q[--N];
        q[N] = null;        // memory error on deletion fix per feedback
        return item;
    }
    
    /**
     * Return a random item, but do not delete it.
     * @throws a java.util.NoSuchElementException if <tt>queue is empty</tt>
     */
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("Queue is Empty");
        int i = StdRandom.uniform(N);
        return q[i];
    }
    
   /**
     * Return an independent iterator over items in random order,
     */
    public Iterator<Item> iterator() {
        return new ReverseArrayIterator();
    }
    
    private class ReverseArrayIterator implements Iterator<Item> {
        private Item[] a;
        private int n;
        
        public ReverseArrayIterator() {
            n = N;
            if (n > 0) {
                a = (Item[]) new Object[N];
                for (int i = 0; i < N; i++) 
                    a[i] = q[i];
                if (n > 1) StdRandom.shuffle(a);
            }
        }
        
        public boolean hasNext() { return n > 0; }
          
        public void remove()      { throw new UnsupportedOperationException(); }
      
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("No more Elements");
            return a[--n];
        }
            
    };
    
    /**
     * Unit testing,
     */
    public static void main(String[] args) {
       RandomizedQueue<String> rq = new RandomizedQueue<String>();
        rq.enqueue(new String("A"));
        rq.enqueue(new String("B"));
        rq.enqueue(new String("c"));
        rq.enqueue(new String("D"));
        rq.enqueue(new String("E"));
        rq.enqueue(new String("F"));
        
        StdOut.println("Iterator API test");
        Iterator it = rq.iterator();
        while (it.hasNext())
            StdOut.print(it.next() + " ");
        StdOut.println("---------------------");
        
        StdOut.println("sample() API test");
        int N = rq.size();
        for (int i = 0; i < N; i++) 
            StdOut.print(rq.sample() + " ");
        StdOut.println("---------------------");
        
        StdOut.println("dequeue() API Test");
        while (!rq.isEmpty())
            StdOut.print(rq.dequeue() + " ");
        StdOut.println("--------------------");
    }
}
