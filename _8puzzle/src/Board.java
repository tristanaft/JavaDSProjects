
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Board {

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    private int[][] tiles;
    private int n;

    public Board(int[][] tiles) {
        this.tiles = tiles; // assume the constructor receives nxn array of integers from 0 to n^2 - 1
        this.n = this.tiles[0].length;
    }

    // string representation of this board
    public String toString() {
        int n = this.n;
        String out = n + "\n";

        for(int i = 1; i < n + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                out += this.tiles[i-1][j-1] + " ";

            }
            out += "\n";
        }


        return out;
    }

    // board dimension n
    public int dimension() {
        return this.tiles[0].length;
    }

    private int[] getIndex(int val) {
        // this just spits out x and y indices given a number between 0 and n^2 - 1
        int xidx = val % this.tiles[0].length;
        int yidx = (val - xidx) / this.tiles[0].length;
        return new int[] {xidx, yidx};
    }

    // number of tiles out of place
    public int hamming() {
        // for an arbitrary number, where is it supposed to be?
        // there is an awkward mismatch... index 0 is supposed to have 1 and index n^2 - 1 is supposed to have 0
        int count = 0;
        int n = this.n;
        for(int i = 1; i < n + 1; ++i) {
            for(int j = 1; j < n+1; j++){
                int correctValue = j + n * (i-1);
                // StdOut.print(correctValue + " ");
                if(i == n && j == n) break; // handle 0 separately
                if(this.tiles[i-1][j-1] != correctValue) {
                    ++count;
                }
            }
        }

        // if(this.tiles[n-1][n-1] != 0) { // check 0 separately...
        //     ++count;
        // }

        return count;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int dist = 0;

        // for an arbitrary number, where is it supposed to be?
        // there is an awkward mismatch... index 0 is supposed to have 1 and index n^2 - 1 is supposed to have 0

        int n = this.n;
        for(int i = 1; i < n + 1; ++i) {
            for(int j = 1; j < n+1; j++){
                int correctValue = j + n * (i-1);
                int currValue;
                if(this.tiles[i-1][j-1] != 0) {
                    currValue = this.tiles[i-1][j-1];
                    // StdOut.print(currValue + " ");
                    //StdOut.print(currValue + " ");
                    int dx = Math.abs(((correctValue-1) % n) - ((currValue-1) % n));
                    int dy = Math.abs(((correctValue-1) / n) - ((currValue-1) / n)); // this should work with integer division
                    dist += dx;
                    dist += dy;
                    // StdOut.print(dx + dy + " ");
                    // StdOut.println();
                }
            }
        }

        return dist;

    }

    // is this board the goal board?
    public boolean isGoal() {
        return (hamming() == 0);
    }

    // does this board equal y?
    public boolean equals(Object y) {
        // equals has weird restrictions...
        if(this == y) return true;
        if(y == null) return false;
        Board that = (Board) y;
        if(this.dimension() != that.dimension()) return false;

        return this.tiles == that.tiles; // is this right?

    }

    private void swap(int[][] tiles, int i1, int j1, int i2, int j2){
        // swap elements in tiles from i1xj1 with i2xj2
        int temp = tiles[i1][j1];
        tiles[i1][j1] = tiles[i2][j2];
        tiles[i2][j2] = temp;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> boardStack = new Stack<>();
        // first, find 0;
        int n = this.n;
        int targetX = 0, targetY = 0;
        for(int i = 1; i < n + 1; ++i) {
            for(int j = 1; j < n+1; ++j) {
                if(this.tiles[i-1][j-1] == 0) {
                    targetX = i;
                    targetY = j;
                    break;
                }
            }
        }

        // ok, have up to 4 moves up down left right, but you can't go outside the nxn grid.

        int[][] tilesCopy = new int[n][n];
        System.arraycopy(this.tiles, 0, tilesCopy,0, this.n*this.n);

        //check up
        if(targetY+1 < n) { // up is available
            swap(tilesCopy, targetX, targetY, targetX, targetY+1);
            boardStack.push(new Board(tilesCopy));
            // ok now swap it back for potentially more
            swap(tilesCopy, targetX, targetY, targetX, targetY+1);
        }

        if(targetY-1 > 0) { // down is available
            swap(tilesCopy, targetX, targetY, targetX, targetY-1);
            boardStack.push(new Board(tilesCopy));
            // ok now swap it back for potentially more
            swap(tilesCopy, targetX, targetY, targetX, targetY-1);
        }

        if(targetX+1 < n) { // right is available
            swap(tilesCopy, targetX+1, targetY, targetX, targetY);
            boardStack.push(new Board(tilesCopy));
            // ok now swap it back for potentially more
            swap(tilesCopy, targetX+1, targetY, targetX, targetY);
        }

        if(targetX-1 > 0) { // left is available
            swap(tilesCopy, targetX-1, targetY, targetX, targetY);
            boardStack.push(new Board(tilesCopy));
            // ok now swap it back for potentially more
            swap(tilesCopy, targetX-1, targetY, targetX, targetY);
        }

        return boardStack;
        
        
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() { // do I just swap two tiles at random? seems vague
        int n = this.n;
        int[][] tilesCopy = new int[n][n];
        System.arraycopy(this.tiles, 0, tilesCopy,0, n*n);
        int v1 = StdRandom.uniformInt(n);
        int v2 = StdRandom.uniformInt(n);
        swap(tilesCopy, v1 % n - 1, v1 / n - 1, v2 % n - 1, v2 / n - 1);
        return new Board(tilesCopy);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] t1 = {{1,2,3},{4,5,6},{7,8,0}};
        int[][] t2 = {{0,1,3},{4,2,5},{7,8,6}};
        int[][] t3 = {{8,1,3},{4,0,2},{7,6,5}};

        Board b1 = new Board(t1);
        Board b2 = new Board(t2);
        Board b3 = new Board(t3);

        StdOut.print(b1.toString());
        StdOut.println("Hamming Distance: " + b1.hamming());
        StdOut.println("Manhattan Distance: " + b1.manhattan());
        StdOut.println();
        StdOut.print(b2.toString());
        StdOut.println("Hamming Distance: " + b2.hamming());
        StdOut.println("Manhattan Distance: " + b2.manhattan());
        StdOut.println();
        StdOut.print(b3.toString());
        StdOut.println("Hamming Distance: " + b3.hamming());
        StdOut.println("Manhattan Distance: " + b3.manhattan());



    }

}