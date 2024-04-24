/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class KdTree {
    private Node root;

    private class Node {
        private Point2D point;
        private boolean isX; // true for x comparisons, false for y
        private int size; // number of nodes below tree containing this.

        private Node left;
        private Node right;

        private RectHV cell;

        private Node(Point2D p) {
            this.point = p;
            size = 1;
        }
    }

    public KdTree()                               // construct an empty set of points
    {
    }

    private void checkNull(Object y) {
        if (y == null) {
            throw new IllegalArgumentException("null argument");
        }
    }


    public boolean isEmpty()                      // is the set empty?
    {
        return size() == 0;
    }

    public int size()                         // number of points in the set
    {
        if (root == null) {
            return 0;
        }
        else {
            return root.size;
        }
    }

    private RectHV newCell(Node current, boolean isLeft) {
        RectHV cell;
        if (current.isX) {
            if (isLeft) {

                cell = new RectHV(current.cell.xmin(), current.cell.ymin(),
                                  current.point.x(), current.cell.ymax());
            }
            else {
                cell = new RectHV(current.point.x(), current.cell.ymin(),
                                  current.cell.xmax(), current.cell.ymax());
            }
        }
        else {
            if (isLeft) {
                cell = new RectHV(current.cell.xmin(), current.cell.ymin(),
                                  current.cell.xmax(), current.point.y());
            }
            else {
                cell = new RectHV(current.cell.xmin(), current.point.y(),
                                  current.cell.xmax(), current.cell.ymax());
            }
        }

        return cell;
    }

    private Node insertStep(Node current, boolean isLeft, Point2D p) {
        // I put this in to reduce copying and pasting the same code over and over...
        // this is a single step forward for the insert algorithm
        // go one step either left or right down the tree
        // if we hit null append a new node and update root size.
        if (current.point.x() == p.x() && current.point.y() == p.y()) {
            return null;
        }

        if (isLeft) {
            if (current.left != null) {
                // current = current.left;
                return current.left;
            }
            else {
                Node newNode = new Node(p);
                root.size++;
                current.left = newNode;
                newNode.isX = !current.isX;
                newNode.cell = newCell(current, true);
                return null;
            }
        }
        else {
            if (current.right != null) {
                return current.right;
            }
            else {
                Node newNode = new Node(p);
                root.size++;
                current.right = newNode;
                newNode.isX = !current.isX;
                newNode.cell = newCell(current, false);
                return null;
            }
        }

    }

    public void insert(
            Point2D p)              // add the point to the set (if it is not already in the set)
    {
        checkNull(p);
        if (root == null) {
            root = new Node(p);
            root.isX = true;
            root.cell = new RectHV(0, 0, 1, 1);
        }
        else {
            Node currNode = root; // start at root
            while (currNode != null) {
                if (currNode.point.x() == p.x() && currNode.point.y() == p.y()) {
                    // the point is in the set, don't add anything
                    break;
                }
                if (currNode.isX) {
                    // x case
                    if (p.x() < currNode.point.x()) {
                        currNode = insertStep(currNode, true, p);
                    }
                    else {
                        currNode = insertStep(currNode, false, p);
                    }
                }
                else {
                    // y case
                    if (p.y() < currNode.point.y()) {
                        currNode = insertStep(currNode, true, p);
                    }
                    else {
                        currNode = insertStep(currNode, false, p);
                    }
                }
            }
        }
    }

    public boolean contains(Point2D p) {
        boolean hasFound = false;
        Node current = root;

        while (current != null) {
            if (current.point.x() == p.x() && current.point.y() == p.y()) {
                hasFound = true;
                break;
            }
            if (current.isX) {
                // compare x
                if (p.x() < current.point.x()) {
                    current = current.left;
                }
                else {
                    current = current.right;
                }
            }
            else {
                // compare y
                if (p.y() < current.point.y()) {
                    current = current.left;
                }
                else {
                    current = current.right;
                }
            }
        }

        return hasFound;

    }


    private List<Node> getAllNodes(Node node) {
        List<Node> nodes = new ArrayList<>();
        if (node != null) {
            nodes.addAll(getAllNodes(node.left));
            nodes.add(node);
            nodes.addAll(getAllNodes(node.right));
        }
        return nodes;
    }

    private void draw(Node node) {
        // draw partition line
        StdDraw.setPenRadius(0.005);
        if (node.isX) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.point.x(), node.cell.ymax(), node.point.x(), node.cell.ymin());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.cell.xmin(), node.point.y(), node.cell.xmax(), node.point.y());
        }

        // draw the point
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        node.point.draw();


    }

    public void draw()                         // draw all points to standard draw
    { // I want to draw recursively somehow, but this will do for now.
        List<Node> nodeList = getAllNodes(root);
        for (Node n : nodeList) {
            draw(n);
        }

    }

    public Iterable<Point2D> range(
            RectHV rect)             // all points that are inside the rectangle (or on the boundary)
    {
        checkNull(rect);
        // List<Point2D> points = rangeSearch(rect, root);
        // return points;
        Stack<Point2D> points = new Stack<>();
        Stack<Node> nodes = new Stack<>();

        if (root == null) {
            return points;
        }

        nodes.push(root);
        while (!nodes.isEmpty()) {
            // get next node
            Node current = nodes.pop();

            // add point to points if it is in rectangle
            if (rect.contains(current.point)) {
                points.push(current.point);
            }

            // add left or right to queue
            if (current.left != null && rect.intersects(current.left.cell)) {
                nodes.push(current.left);
            }
            if (current.right != null && rect.intersects(current.right.cell)) {
                nodes.push(current.right);
            }
        }


        return points;

    }

    private Point2D nearestSearch(Point2D queryPoint, Node current, Point2D champ) {

        // we need to check along the partition line(?) of a point to see whether the
        // subtree is worth traversing?

        if (current == null) {
            return champ;
        }
        if (current.point.equals(queryPoint)) {
            return queryPoint;
        }

        if (current.point.distanceSquaredTo(queryPoint) < champ.distanceSquaredTo(queryPoint)) {
            champ = current.point;
        }
        double rectDistance; // trying the rect distance now...
        if (current.left != null) {
            rectDistance = current.left.cell.distanceSquaredTo(queryPoint);
            if (rectDistance <= champ.distanceSquaredTo(queryPoint)) {
                champ = nearestSearch(queryPoint, current.left, champ);
            }
        }
        if (current.right != null) {
            rectDistance = current.right.cell.distanceSquaredTo(queryPoint);
            if (rectDistance <= champ.distanceSquaredTo(queryPoint)) {
                champ = nearestSearch(queryPoint, current.right, champ);
            }
        }

        return champ;
    }

    public Point2D nearest(
            Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        checkNull(p);
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return nearestSearch(p, root, root.point);
    }

    public static void main(
            String[] args)                  // unit testing of the methods (optional)
    {
        String filename = args[0];
        In in = new In(filename);
        // PointSET brute = new PointSET();
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            // brute.insert(p);
        }
        RectHV rect = new RectHV(0.25, 0.25, 0.75, 0.75);
        kdtree.draw();
        for (Point2D pt : kdtree.range(rect)) {
            StdDraw.setPenRadius(0.01);
            StdDraw.setPenColor(StdDraw.GREEN);
            pt.draw();
        }
        rect.draw();


    }
}
