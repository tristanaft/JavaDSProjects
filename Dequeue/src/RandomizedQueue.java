import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {


    private Item[] items;
    private int size;

    // construct an empty randomized queue
    public RandomizedQueue() {
        this.size = 0;
        this.items = (Item[]) new Object[1]; // I don't think there is a way to get around this unchecked cast

    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return this.size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return this.size;
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity]; // again, awkward unchecked cast
        if (size >= 0) System.arraycopy(items, 0, copy, 0, size);
        items = copy;

    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new NullPointerException("RandomizedQueue can't accept null");
        if (size == items.length) {             // if array is full
            resize(2 * items.length);   // double the size
        }
        items[size] = item;
        size++;

    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Randomized Queue is empty");
        // get a random item
        int randIdx = StdRandom.uniformInt(size);
        // get the corresponding item for output
        Item output = items[randIdx];
        // now the spot is "empty", put the item at the end of the array into the empty space
        items[randIdx] = items[size-1]; // size starts at 1 but array idx starts at 0
        // and erase what was previously there
        items[size-1] = null;
        // and decrement size
        --size;
        // resize if necessary to conserve space
        if (size > 0 && size == items.length/4) resize(items.length/2);

        return output;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        // this is basically the same as dequeue as above, but we don't alter the array
        if (isEmpty()) throw new NoSuchElementException("Randomized Queue is empty");
        // get a random item
        int randIdx = StdRandom.uniformInt(size);
        // get the corresponding item for output
        return items[randIdx];
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private int current = 0;

        public RandomizedQueueIterator() { // need constructor to set up randInt
            int[] randIntArray = new int[size];
            for (int i = 0; i < size; i++) {
                randIntArray[i] = i; // get a set of integers from 0 to queueSize
            }
            StdRandom.shuffle(randIntArray); // now randIntArray is randomized
            // can randIntArray be null?
        }
        @Override
        public boolean hasNext() { return current != size; }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removal is not supported by RandomizedQueue Iterator.");
        }
        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("No more items to iterate through in RandomizedQueue");
            Item item = items[current]; // I guess this cast is also necessary
            ++current;
            return item;
        }

    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        // main must call directly every public constructor to verify they work as prescribed
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        queue.enqueue(1);
        queue.enqueue(1);
        queue.enqueue(3);
        queue.enqueue(2);
        queue.dequeue();
    }

}