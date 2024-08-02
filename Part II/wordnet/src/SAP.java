import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

import java.util.Collections;

public class SAP {

    final private Digraph dg;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.dg = G;
    }

    private boolean validVertex(int v) {
        return (-1 < v && v < dg.V());
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!validVertex(v) || !validVertex(w)) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        return length(Collections.singleton(v), Collections.singleton(w));

    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!validVertex(v) || !validVertex(w)) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        return ancestor(Collections.singleton(v), Collections.singleton(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths bfdpV = new BreadthFirstDirectedPaths(this.dg, v);
        BreadthFirstDirectedPaths bfdpW = new BreadthFirstDirectedPaths(this.dg, w);

        int target = ancestor(v, w);
        if (target != -1) {
            return bfdpV.distTo(target) + bfdpW.distTo(target);
        }
        else {
            return -1;
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int shortestLength = Integer.MAX_VALUE;
        int ancestor = -1;
        BreadthFirstDirectedPaths bfdpV = new BreadthFirstDirectedPaths(this.dg, v);
        BreadthFirstDirectedPaths bfdpW = new BreadthFirstDirectedPaths(this.dg, w);
        //dg.V();
        // I guess we can just iterate through every vertex in dg
        // I there has to be some way to recursively look, but this
        // is way easier...
        int maxIdx = dg.V();
        for (int i = 0; i < maxIdx; ++i) {
            if (bfdpV.hasPathTo(i) && bfdpW.hasPathTo(i)) {
                int pathLength = bfdpV.distTo(i) + bfdpW.distTo(i);
                if (pathLength < shortestLength) {
                    ancestor = i;
                    shortestLength = pathLength;
                }
            }
        }
        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // not currently doing any testing here.
        // testing is in WordNet or Outcast
    }
}