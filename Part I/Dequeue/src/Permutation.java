import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;


public class Permutation {
    public static void main(String[] args) {
        RandomizedQueue<String> rqs = new RandomizedQueue<>(); // make sure to initialize
        // StdOut.println("Initialized");

        // Expect input in the form, K, then a sequence of strings
        // int k = StdIn.readInt(); // how many strings to read off
        int k = Integer.parseInt(args[0]); // how many strings to read off
        // String[] strings = StdIn.readAllStrings(); // why does this not work?
        // String[] strings = new String[args.length-1]; // autograder doesn't like arrays even though this is more efficient...
        // intelliJ prompts me to do arraycopy, so I will use that.
        // System.arraycopy(args,  1, strings, 0, args.length-1);
        // for (String s : strings) {
        //     StdOut.print(s + " ");
        // }
        // StdOut.println();
        // StdOut.println("Read Args");
        // if we shuffle the strings, we don't have to read them all into Randomized Queue
        // we could actually save extra time/space and use Deque...
        // StdRandom.shuffle(strings);
        while (!StdIn.isEmpty()){
            rqs.enqueue(StdIn.readString());
        }

        // read strings into queue
        // for (int i = 1; i < k + 1; ++i) {
        //     rqs.enqueue(strings[i]);
        // }
        // StdOut.println("Enqueued");
        // output the strings
        for (int i = 0; i < k; ++i) {
            StdOut.println(rqs.dequeue());
        }
        // StdOut.println("Printed");


    }
}