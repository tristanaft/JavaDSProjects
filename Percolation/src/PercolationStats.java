import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdRandom;


public class PercolationStats {
    private final double[] thresholdValues;
    private final int numTrials;
    private final double CONFIDENCE_95 = 1.96; // this is a magic number for the 95 percent confidence interval

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        this.numTrials = trials;
        this.thresholdValues = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            boolean hasPercolated = false;
            while (!hasPercolated) { // while the system hasn't percolated
                // choose a site at random
                boolean validChoice = false;
                while (!validChoice) {
                    int xIdx = StdRandom.uniformInt(n) + 1;
                    int yIdx = StdRandom.uniformInt(n) + 1;
                    if (!perc.isOpen(xIdx, yIdx)) { // if this is NOT open, it is valid and stop looping
                        validChoice = true;
                        perc.open(xIdx, yIdx);
                        hasPercolated = perc.percolates();
                    }
                }

            }
            // Ok, so perc has now percolated
            this.thresholdValues[i] = (double) perc.numberOfOpenSites() / (n*n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(this.thresholdValues);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(this.thresholdValues);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return (this.mean() - (CONFIDENCE_95) * this.stddev() / (Math.sqrt(this.numTrials)));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return (this.mean() + (CONFIDENCE_95) * this.stddev() / (Math.sqrt(this.numTrials)));
    }

    // test client (see below)
    public static void main(String[] args) {
        // this takes 2 command line args, n and T
        // n is the grid size, T is the number of trials
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats ps = new PercolationStats(n, t);
        StdOut.println("mean = " + ps.mean());
        StdOut.println("stddev = " + ps.stddev());
        StdOut.println("95% confidence interval = "
                + "[" + ps.confidenceLo() + ", " + ps.confidenceHi() + "]");
    }

}

