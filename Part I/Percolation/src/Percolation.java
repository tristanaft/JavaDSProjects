import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class Percolation {
    // NOTE PUT EDGE CASES IN OPEN ISOPEN ISFULL

    private final boolean[][] grid;
    // private int[][] connections;
    private int numOpen; // number of open sites
    private final int n; // this is to save the value of n

    private final WeightedQuickUnionUF uf; // the finder DS to find if percolation is true

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {          // here, I am assuming 0 is closed 1 open
        if (n < 1) { throw new IllegalArgumentException("N must be greater than 0"); }
        // this.grid = new int[n][n];   // int always has an initial value of 0
                                        // so this should be enough
        this.uf = new WeightedQuickUnionUF(n*n+2); // 2 extra virtual sites
        // the trick described in the lecture was to have 2 virtual sites to check percolation
        this.grid = new boolean[n][n];
        for (int i = 1; i <= n; i++) {
            // need to connect top and bottom rows to elements 0 and n**2+1 respectively
            // note the awkward mismatch btw indices of the grid starting at 1 and array indices
            this.uf.union(i, 0); // normal grid ends at element n^2 in uf.
            this.uf.union(n*(n-1) + i, n*n + 1);
        }
        this.n = n;
    }

    private int gridToFlat(int row, int column) {
        // this takes a row and column and changes it to an index for uf to handle
        return this.n*(row-1) + column;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) { // by convention, goes from 1 to n
        int centerIndex = gridToFlat(row, col);
        int otherIndex;
        if (row > this.n || col > this.n || row < 1 || col < 1) throw new IllegalArgumentException("Open called outside the grid");
        if (!grid[row - 1][col - 1]) {   // if empty
            grid[row-1][col-1] = true;     // make open
            this.numOpen += 1;         // increment number of open sites
            // check around the site and connect it to all other adjacent open sites
            if (row-1 > 0) { // left is in grid
                otherIndex = gridToFlat(row-1, col);
                if (isOpen(row-1, col)) { // is this site open?
                    this.uf.union(centerIndex, otherIndex);

                }
            }
            if (row+1 < this.n+1) { // right is in grid
                otherIndex = gridToFlat(row+1, col);
                if (isOpen(row+1, col)) { // is this site open?
                    this.uf.union(centerIndex, otherIndex);

                }

            }
            if (col-1 > 0) { // down is in grid
                otherIndex = gridToFlat(row, col-1);
                if (isOpen(row, col-1)) {
                    this.uf.union(centerIndex, otherIndex);

                }
            }
            if (col+1 < this.n+1) { // left is in grid
                otherIndex = gridToFlat(row, col+1);
                if (isOpen(row, col+1)) {
                    this.uf.union(centerIndex, otherIndex);

                }
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {    // by convention, goes from 1 to n
        if (row > this.n || col > this.n || row < 1 || col < 1) throw new IllegalArgumentException("isOpen called outside the grid");
        return (grid[row-1][col-1]);
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row > this.n || col > this.n || row < 1 || col < 1) throw new IllegalArgumentException("isFull called outside the grid");
        // a site is FULL iff it is connected to the top.
        // I have 2 virtual sites, at positions 0 and n^2 + 1 in uf
        // top is 0
        int idx = gridToFlat(row, col);

        // for something to be full must be connected to top AND OPEN
        return ((this.uf.find(idx) == this.uf.find(0)) && isOpen(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return (this.numOpen);
    }

    // does the system percolate?
    public boolean percolates() {
        // I have 2 virtual sites, at positions n^2 and n^2 + 1 in uf
        // these represent top and bottom, so check if those 2 are connected
        // I guess I can use isFull here... not really a reason to though
        // return (this.uf.find(n^2) == this.uf.find(n^2+1));

        // there is the awkward case of n = 1 where this doesn't work
        // I think I just have to deal with that manually.
        if (this.n == 1){
            return grid[0][0];
        }
        else{
            return (this.uf.find(0) == this.uf.find(n*n+1));
        }


    }

    // test client (optional)
    public static void main(String[] args) {
        // edge cases, n = 1 and n = 2
        Percolation p1 = new Percolation(1);
        Percolation p2 = new Percolation(2);
        if (p1.percolates()) {
            StdOut.println("n = 1 percolates");
        }

        if (p2.percolates()) {
            StdOut.println("n = 1 percolates");
        }



        // try init
        StdOut.println("Initializing percolation with n=10");
        int n = 10;
        Percolation p = new Percolation(n);
        // ok, try opening a few sites...
        StdOut.println("Opening 5,5 and 5,6");
        p.open(5, 5);
        p.open(5, 6);
        StdOut.println("\n");
        StdOut.println("Are they open?");
        if (p.isOpen(5, 5)) StdOut.println("Site 1 is open");
        if (p.isOpen(5, 6)) StdOut.println("Site 2 is open");
        StdOut.println("How many open sites?");
        int c = p.numberOfOpenSites();
        StdOut.println(c);
        StdOut.println("what if we try to open 5,5 again?");
        p.open(5, 5);
        StdOut.println(c);

        StdOut.println("Try edge cases: 1,1 and 10,10");
        p.open(10, 10);
        p.open(1, 1);

        StdOut.println("Ok, I will open 2,1 3,1 and 4,1 and check if 4,1 is full");
        p.open(2, 1);
        p.open(3, 1);
        p.open(4, 1);
        if (p.isFull(4, 1)) StdOut.println("4,1 is full");
        StdOut.println("Ok, I will open 4,2 through to 4,10 and check if 4,10 is full");
        for (int i = 2; i <= 10; i++) {
            p.open(4, i);
        }
        if (p.isFull(4, 10)) StdOut.println("4,10 is full");
        StdOut.println("Now, does the system percolate? It should not.");
        if (p.percolates()) StdOut.println("The system percolates");
        StdOut.println("Ok, now let's go down from 4,10 to 10,10 and see if the system percolates");
        for (int i = 4; i <= 10; i++) {
            p.open(i, 10);
        }
        if (p.percolates()) StdOut.println("The system percolates");

        // errors all check out
        // StdOut.println("Ok... I think that is all the main checks... other than the error ones");
        // p.open(0,5); //error as expected
        // p.open(11,2); //error as expected
        // p.isOpen(0,5);
        // p.isOpen(11,2);


    }
}