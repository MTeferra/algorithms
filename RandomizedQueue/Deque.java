/**
 *  The <tt>Deque</tt> class is a <em>double-ended queue or deque</em>
 *  that is a generalization of a stack and a queue that supports inserting and 
 *  removing items from either the front or the back of the data structure.
 *  <p>
 * Requirement; The deque implementation must support each deque operation in 
 *              constant worst-case time and use space proportional to the number
 *              of items currently in the deque. Additionally, it supports the 
 *              operations next() and hasNext() (plus construction) in constant 
 *              worst-case time and use a constant amount of extra space per 
 *              iterator. 
 *  <p>
 *
 *  @author Michael Teferra
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    
    private Node<Item> first;
    private Node<Item> last;
    private int qSize;
    
    private class Node<Item> {
        private Item item;
        private Node<Item> next;
        private Node<Item> prev;
    }
    
    /**
     * Construct an empty deque,
     */
    public Deque() {
        first = null;
        last  = null;
        qSize = 0;
    }
    
    /**
     * Is the deque empty ?
     */
    public boolean isEmpty()  {
        return (first == null) && (last == null);
    }
     
    /**
     * Return the number of items on the deque
     */
    public int size() {
        return qSize;
    }
    
    /**
     * Insert the item at the front,
     * @throws a NullPointerException if <tt>item is null</tt>
     * @param item the item to be inserted
     */
    public void addFirst(Item item) {
        if (item == null) throw new NullPointerException();
        
        Node<Item> oldFirst = first;
        first               = new Node<Item>();
        first.item          = item;
        first.next          = oldFirst;
        first.prev          = null;
        if (oldFirst == null) last = first;
        else          oldFirst.prev = first;   // fix failure case from feedback
        qSize++;
    }
    
    /**
     * Insert the item at the end,
     * @throws a NullPointerException if <tt>item is null</tt>
     * @param item the item to be inserted
     */
    public void addLast(Item item) {
        if (item == null) throw new NullPointerException();
        
        Node<Item> oldLast = last;
        last               = new Node<Item>();
        last.item          = item;
        last.next          = null;
        last.prev          = oldLast;
        if (oldLast == null) first        = last;
        else                 oldLast.next = last;
        qSize++;
    }   
    
    /**
     * Delete and return the item at the front,
     * @throws a java.util.NoSuchElementException if <tt>queue is empty</tt>
     */
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is Empty");
        
        Item item = first.item;
        first     = first.next;
        if (first == null) last       = first;
        else               first.prev = null;
        qSize--;
        
        return item;
    }
    
    /**
     * Delete and return the item at the end,
     * @throws a java.util.NoSuchElementException if <tt>queue is empty</tt>
     */
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is Empty");      
        Item item = last.item;
        last      = last.prev;
        if (last == null) first     = last;
        else              last.next = null;
        qSize--;
        
        return item;
    }
    
    /**
     * Return an iterator over items in order from front to end,
     */
    public Iterator<Item> iterator() {
        return new ListIterator<Item>(first);
    }
      
    private class ListIterator<Item> implements Iterator<Item> {
        private Node<Item> current;
        
        public ListIterator(Node<Item> first) {
            current = first;
        }
        
        public boolean hasNext()  { return current != null; }
        
        public void remove()      { throw new UnsupportedOperationException(); }
        
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current   = current.next; 
            return item;
        }
    }
    
    private static void printQ(Deque<String> dq) {
        Iterator<String> iter = dq.iterator();
        while (iter.hasNext()) {
            StdOut.print(iter.next() + " ");
        }
        StdOut.println("");
        StdOut.println("Size = " + dq.size());
    }     
    
    /**
     * Unit testing,
     */
    public static void main(String[] args) {
        
        Deque<String> dq = new Deque<String>();
        dq.addFirst("first");
        dq.addLast("middle");
        dq.addLast("last");
        dq.addFirst("zero");
        
        printQ(dq);
        dq.removeFirst();
        printQ(dq);
        dq.removeLast();
        dq.removeLast();
        printQ(dq);
        dq.removeLast();
        
        try {
            dq.removeLast();
        } catch (NoSuchElementException e) {
            StdOut.println("empty");
        }
        
        // feedback related test
        dq.addFirst("first");
        dq.removeLast();
        StdOut.println("isEnpty() test1: " + dq.isEmpty());
        dq.addFirst("first");
        dq.addFirst("zero");
        dq.removeLast();
        StdOut.println("isEmpty() test2: " + dq.isEmpty());
        StdOut.println("last element in queue is: " + dq.removeLast());
         
    }
}

