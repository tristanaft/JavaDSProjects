import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoggleSolver {


    private Set<String> foundWords;

    private BoggleTrie trie;

    // BoggleBoard board;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trie = new BoggleTrie(dictionary);

    }

    private static List<int[]> findNeighbors(BoggleBoard board, int row, int col) {
        List<int[]> neighbors = new ArrayList<>();

        // Define the directions for the neighbors
        int[] rowDirections = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colDirections = {-1, 0, 1, -1, 1, -1, 0, 1};

        // Iterate over all possible directions
        for (int k = 0; k < rowDirections.length; k++) {
            int newRow = row + rowDirections[k];
            int newCol = col + colDirections[k];

            // Check if the new position is within bounds
            if (isInBounds(board, newRow, newCol)) {
                neighbors.add(new int[]{newRow, newCol});
            }
        }

        return neighbors;
    }

    private static boolean isInBounds(BoggleBoard board, int row, int col) {
        return row >= 0 && row < board.rows() && col >= 0 && col < board.cols();
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        foundWords = new HashSet<>();
        // this.board = board;

        boolean[][] visited = new boolean[board.rows()][board.cols()];
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                depthFirstSearch(row, col, visited, board, "");
            }
        }


        return foundWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int len = word.replace("q", "").length();
        if (!trie.get(word)) {
            return 0;
        } else {
            if (len < 5) return 1;
            else if (len == 5) return 2;
            else if (len == 6) return 3;
            else if (len == 7) return 5;
            else return 11;

        }
    }

    private void depthFirstSearch(int row, int col, boolean[][] visited, BoggleBoard board, String word) {

        // shouldnt need this, but just in case
        if (row < 0 || row >= board.rows() || col < 0 || col >= board.cols() || visited[row][col]) {
            return;
        }

        visited[row][col] = true;


        char c = board.getLetter(row, col);
        // word += c;
        String newWord = word + c;
        // have to include exception for Q
        if (c == 'Q') newWord += 'U';
        //StdOut.println(newWord);

        // only have to start checking the word once we have at least 3 characters
        if (newWord.length() > 2 && trie.get(newWord)) {
            foundWords.add(newWord);
        }


        // This is where keeping everything in separate classes can be really annoying.
        // If everything was all together, I could be going down the tree WHILE also traversing the board
        // however, since everything is separate, I have to build the tree and then do a complete search
        // every time I add a new character... which seems really inefficient to me...

        // Only keep going if the word is a valid prefix
        if (trie.isPrefix(newWord)) {
            // StdOut.println("valid prefix");
            // invoke on valid neighbors
            // what findNeighbors is never empty, but sometimes they are all visited...
            for (int[] point : findNeighbors(board, row, col)) {
                // check if the coord is valid (not visited)
                int neighborRow = point[0];
                int neighborCol = point[1];
                if (!visited[neighborRow][neighborCol]) {
                    depthFirstSearch(neighborRow, neighborCol, visited, board, newWord);
                }
            }
        }


        // for(int[] point : findNeighbors(board, row, col)){
        //     // check if the coord is valid (not visited)
        //     int neighborRow = point[0];
        //     int neighborCol = point[1];
        //     if(!visited[neighborRow][neighborCol]) {
        //         depthFirstSearch(neighborRow, neighborCol, visited, board, newWord);
        //     }
        // }


        // after everything over, set visited to false, remove last char in word
        // actually, word itself wasn't modified at all in this loop... so maybe it is fine?

        visited[row][col] = false;

    }
}