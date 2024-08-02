/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

import java.util.ArrayList;
import java.util.List;

public class PointSET {
    private SET<Point2D> set;

    public PointSET()                               // construct an empty set of points
    {
        set = new SET<>();
    }

    public boolean isEmpty()                      // is the set empty?
    {
        return set.isEmpty();
    }

    public int size()                         // number of points in the set
    {
        return set.size();
    }

    public void insert(
            Point2D p)              // add the point to the set (if it is not already in the set)
    {
        checkNull(p);
        set.add(p);
    }

    public boolean contains(Point2D p)            // does the set contain point p?
    {
        checkNull(p);
        return (set.contains(p));
    }

    public void draw()                         // draw all points to standard draw
    {
        for (Point2D p : set) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(
            RectHV rect)             // all points that are inside the rectangle (or on the boundary)
    {
        // this is brute force method
        checkNull(rect);
        List<Point2D> points = new ArrayList<>();
        for (Point2D p : set) {
            if (rect.contains(p)) {
                points.add(p);
            }
        }
        return points;
    }

    public Point2D nearest(
            Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        // this is brute force method, goes through every point.
        checkNull(p);
        Point2D champ = null;
        double champDist = Double.POSITIVE_INFINITY;
        for (Point2D q : set) {
            double currDist = q.distanceSquaredTo(p);
            if (currDist < champDist) {
                champ = q;
                champDist = currDist;
            }
        }

        return champ;
    }

    private void checkNull(Object y) {
        if (y == null) {
            throw new IllegalArgumentException("null argument");
        }
    }

    public static void main(
            String[] args)                  // unit testing of the methods (optional)
    {
    }
}
