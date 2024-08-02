import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class MoveToFront {

    private static final int R = 256;

    public static void encode() {

        LinkedList<Character> chars = new LinkedList<>();
        for (int i = 0; i < R; i++) {
            chars.add((char) i);
        }

        char[] input = BinaryStdIn.readString().toCharArray();

        for (char ch : input) {
            // read char
            int index = chars.indexOf(ch);
            BinaryStdOut.write((char) index);

            // move to front
            if (index != 0) {
                chars.remove(index);
                chars.addFirst(ch);
            }
        }
        BinaryStdOut.close();
    }


    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {

        // set up initial character array
        LinkedList<Character> chars = new LinkedList<>();
        for (int i = 0; i < R; i++) {
            chars.add((char) i);
        }

        // read input
        char[] input = BinaryStdIn.readString().toCharArray();

        for(char index : input){
            char ch = chars.get(index);
            BinaryStdOut.write(ch);

            // move to front
            if (index != 0) {
                chars.remove(index);
                chars.addFirst(ch);
            }

        }
        BinaryStdOut.close();


    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        } else {
            throw new IllegalArgumentException();
        }



    }

}