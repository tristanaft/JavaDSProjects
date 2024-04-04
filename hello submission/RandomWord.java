/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {
    public static void main(String[] args) {
        // Use Knuth's method, don't store the strings

        double count = 0;
        String chosenString = "";
        String newString;
        while (!StdIn.isEmpty()) {
            count++;
            newString = StdIn.readString();
            // StdOut.println(newString);
            if (StdRandom.bernoulli(1 / count)) {
                chosenString = newString;
            }
        }
        StdOut.println(chosenString);
        // StdOut.println("Test print...");
    }
}
