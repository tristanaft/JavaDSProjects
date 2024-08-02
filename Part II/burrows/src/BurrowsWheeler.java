import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String input = BinaryStdIn.readString();
        int length = input.length();
        CircularSuffixArray csa = new CircularSuffixArray(input);
        char[] t = new char[length];

        for (int i = 0; i < length; i++) {
            int idx = csa.index(i);

            if (idx == 0) {
                t[i] = input.charAt(length - 1);
                BinaryStdOut.write(i);
            } else {
                t[i] = input.charAt(csa.index(i) - 1);
            }
        }

        for (char ch : t) {
            BinaryStdOut.write(ch, 8);
        }
        BinaryStdOut.close();

    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int n = BinaryStdIn.readInt();
        char[] t = BinaryStdIn.readString().toCharArray();

        int length = t.length;
        int[] next = new int[length];

        int[] count = new int[R + 1];

        for (int i = 0; i < length; i++) {
            count[t[i] + 1] += 1;
        }
        for (int i = 0; i < R; i++) {
            count[i + 1] += count[i];
        }
        for (int i = 0; i < length; i++) {
            next[count[t[i]]++] = i;
        }
        for (int i = 0; i < length; i++) {
            n = next[n];
            BinaryStdOut.write(t[n]);
        }

        BinaryStdOut.close();


    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String input = args[0];

        if (input.equals("-")) {
            transform();
        } else if (input.equals("+")) {
            inverseTransform();
        } else {
            throw new IllegalArgumentException();
        }

    }

}