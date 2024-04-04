import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import edu.princeton.cs.algs4.Stack;

public class FastCollinearPoints {
    private LineSegment[] savedSegments;
    private int numSegments;

    public FastCollinearPoints(Point[] points) { // finds all line segments containing 4 or more points
        // handle exceptions
        if (points == null) throw new IllegalArgumentException("Passed null to BruteCollinearPoints.");

        Point[] pointsCopy = points.clone();

        for (Point point : pointsCopy) {
            if (point == null) throw new IllegalArgumentException("Point passed to BruteCollinearpoints was null.");
        }
        for (int i = 0; i < pointsCopy.length; i++) {
            for (int j = i + 1; j < pointsCopy.length; j++) {
                if (pointsCopy[i].compareTo(pointsCopy[j]) == 0)
                    throw new IllegalArgumentException("Duplicate points passed to BruteCollinearPoints.");
            }
        }

        Stack<LineSegment> lineSegmentStack = new Stack<>();

        // ok, so the idea is to sort according to the slope WRT a specific element.
        // I need a second array, otherwise I am going to have to re-sort the array every time I want to pick a new point...
        Point[] pointsCopy2 = points.clone();
        Arrays.sort(pointsCopy);

        for (int i = 0; i < pointsCopy.length; i++) {
            Point p = pointsCopy2[i];
            Arrays.sort(pointsCopy); // sort by slope wrt p
            Arrays.sort(pointsCopy, p.slopeOrder()); // sort by slope wrt p


            // check if any 3 or more adjacent points in the sorted order have equal slopes wrt p
            double currentSlope = pointsCopy[0].slopeTo(pointsCopy[1]); // have to start at 1st element
            int count = 0;
            for (int j = 1; j < pointsCopy.length; j++) {
                double slope = pointsCopy[0].slopeTo(pointsCopy[j]);
                // StdOut.println("Reference Point: " + p.toString());
                // for (Point point: pointsCopy) {
                //     StdOut.println(point.toString());
                // }
                // StdOut.println();

                if (slope == currentSlope) { // if this matches what we have saved
                    ++count; // increment count

                } else { // hit a new slope

                    if (count >= 3) { // ok, current run ended, and we have 3 or more points with the same slope
                        // to prevent doubling up, I will introduce this comparison.
                        // without this, the stack would get both p->r and r->p,

                        // ok, we also have the issue of if we have p->q->r with q btw p and r
                        // the program picks up p->r and q-> r, need to save and compare FIRST ELEMENTS
                        // That is why we need the second condition.


                        if (p.compareTo(pointsCopy[j - 1]) < 0 && p.compareTo(pointsCopy[j - count]) <= 0) {
                            // StdOut.println(p.toString());
                            // StdOut.println(pointsCopy[j - count]);
                            // StdOut.println();

                            lineSegmentStack.push(new LineSegment(p, pointsCopy[j - 1]));
                            this.numSegments++;
                        }
                    }
                    // we now have a new current slope, and we reset the count
                    currentSlope = slope;
                    count = 1;

                }
            }

            // we iterated through j, but we can have the case where we get 5 same slopes all at the end
            // and, we didn't push a segment to the stack because we didn't hit a different slope
            if (count >= 3) {
                if (p.compareTo(pointsCopy[pointsCopy.length - 1]) < 0 && p.compareTo(pointsCopy[pointsCopy.length - count]) <= 0) {
                    lineSegmentStack.push(new LineSegment(p, pointsCopy[pointsCopy.length - 1]));
                    this.numSegments++;
                }
            }
        }
        // All the line segments should be in the stack, so empty it into the array
        this.savedSegments = new LineSegment[this.numSegments];
        for (int i = 0; i < this.numSegments; i++) {
            this.savedSegments[i] = lineSegmentStack.pop();
        }


    }

    public int numberOfSegments() { // the number of line segments
        return this.numSegments;
    }

    public LineSegment[] segments() { // the line segments
        return this.savedSegments;
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}

