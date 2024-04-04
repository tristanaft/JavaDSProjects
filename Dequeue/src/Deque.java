import edu.princeton.cs.algs4.StdOut;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class Deque<Item> implements Iterable<Item> {

    private class Node {
        // Note that this is different from the original implementation.
        // Here, this is a DOUBLY linked list, so nodes also have a pointer to previous element.
        Item item;
        Node next;
        Node prev;
    }

    private Node first, last;
    private int size;

    // construct an empty deque
    public Deque() {

    }


    // is the deque empty?
    public boolean isEmpty() {
        // return first == null;
        // I will try this? Might break though.
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        // man this is bad for linked lists, don't we have to iterate through the whole thing?
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("Attempted to add null to Deque.");
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
        if (isEmpty()) last = first;
        else oldFirst.prev = first;
        size++;

    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("Attempted to add null to Deque.");
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty()) first = last;
        else {
            oldLast.next = last;
            last.prev = oldLast;
        }
        size++;

    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Attempted to remove from empty Deque");
        size--;
        Item item = first.item;
        first = first.next; // if there is one item, this will set first to null.
        if (isEmpty()) {
            last = null;
        } else {
            first.prev = null; // prevent loitering
        }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Attempted to remove from empty Deque");
        size--;
        Item item = last.item;
        last = last.prev;
        if (isEmpty()) {
            first = null;
        } else {
            last.next = null; // prevent loitering
        }

        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        // return new DequeIterator(); // while intelliJ is fine with this, the autograder is NOT?
        return new DequeIterator();
    }


    private class DequeIterator implements Iterator<Item> {
        private Node current = first;
        @Override
        public boolean hasNext() { return current != null; }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removal is not supported by Deque Iterator.");
        }
        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("No more items to iterate through in Deque");
            Item item = current.item; // Is this unchecked cast necessary?
            current = current.next;
            return item;
        }

    }

    // unit testing (required)
    public static void main(String[] args) {
        // must call every public constructor and method to help verify they work
        Deque<Integer> dqInt = new Deque<>();
        if (dqInt.isEmpty()) StdOut.println("dqInt is empty as expected");
        // ok, do dqInt is empty... let's add one element at a time and see if we can remove front and back?
        StdOut.println("Ok, now let's addFirst 10 to the queue");
        dqInt.addFirst(10);
        StdOut.println("Can I remove from the front?");
        StdOut.println(dqInt.removeFirst());
        if (dqInt.isEmpty()) StdOut.println("dqInt is empty as expected");
        StdOut.println("Ok, try again and remove from back");
        dqInt.addLast(9);
        StdOut.println("Can I remove from the back?");
        StdOut.println(dqInt.removeLast());




    }

}