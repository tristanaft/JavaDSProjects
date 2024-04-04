import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import edu.princeton.cs.algs4.Stack;

public class BruteCollinearPoints {
    private LineSegment[] savedSegments;
    private int numSegments;

    public BruteCollinearPoints(Point[] points) { // finds all line segments containing 4 points

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

        // ok now we can actually do stuff...
        Arrays.sort(pointsCopy);
        Stack<LineSegment> lineSegmentStack = new Stack<>(); // put all the segments onto the stack, then build the line segment array
        for (int i = 0; i < pointsCopy.length; i++) {
            for (int j = i + 1; j < pointsCopy.length; j++) {
                double s1 = pointsCopy[i].slopeTo(pointsCopy[j]);
                for (int k = j + 1; k < pointsCopy.length; k++) {
                    double s2 = pointsCopy[i].slopeTo(pointsCopy[k]);
                    for (int m = k + 1; m < pointsCopy.length; m++) {
                        double s3 = pointsCopy[i].slopeTo(pointsCopy[m]);
                        if (s1 == s2 && s2 == s3) {
                            ++this.numSegments;
                            LineSegment ls = new LineSegment(pointsCopy[i], pointsCopy[m]);
                            lineSegmentStack.push(ls);
                        }
                    }
                }
            }
        }

        // ok, all line segments are in the stack
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}
