import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stack;

import java.util.Comparator;

public class Solver {

    private boolean isSolvable = false;
    private int minMoves = 0;
    private Comparator<SearchNode> manhCompObj = new manhattanCompare();
    private Comparator<SearchNode> hammCompObj = new hammingCompare();
    private SearchNode root;


    // define search node class that we add to the heap
    private class SearchNode {
        Board board = null;
        SearchNode previousNode = null;
        int moves = 0;
        int manhattanPriority = 0;
        int hammingPriority = 0;
        SearchNode(Board current, int moves, SearchNode prev) {
            this.board = current;
            this.previousNode = prev;
            this.moves = moves;
            this.manhattanPriority = moves + current.manhattan();
            this.hammingPriority = moves + current.hamming();
        }
    }

    // I could sort by either manhattan or hamming, I have implemented both here, but I will go with manhattan

    private class manhattanCompare implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode left, SearchNode right) {
            return Integer.compare(left.manhattanPriority, right.manhattanPriority);
        }
    }

    private class hammingCompare implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode left, SearchNode right) {
            return Integer.compare(left.hammingPriority, right.hammingPriority);
        }
    }




    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new java.lang.IllegalArgumentException("Passed null initial board to Solver.");
        // do I pass it a new comparator object or should I pass it one I initialized outside of Solver?
        //MinPQ<SearchNode> searchNodeMinPQ = new MinPQ<>(new manhattanCompare());
        // I could use hamming instead...
        // MinPQ<SearchNode> searchNodeMinPQ = new MinPQ<>(hammCompObj);

        // according to the problem statement, either the initial board or a twin of it have the solution
        // I guess the only way to do this is to simultaneously try to solve the initial and twin boards...
        MinPQ<SearchNode> mainMinPQ = new MinPQ<>(manhCompObj);
        MinPQ<SearchNode> twinMinPQ = new MinPQ<>(manhCompObj);

        SearchNode root = new SearchNode(initial, 0, null);
        mainMinPQ.insert(root);
        // don't need to worry about saving the twin root
        twinMinPQ.insert(new SearchNode(initial.twin(), 0, null));

        while(!mainMinPQ.isEmpty() || twinMinPQ.isEmpty()){
            SearchNode mainNode = mainMinPQ.delMin();
            SearchNode twinNode = twinMinPQ.delMin();
            // StdOut.println(mainNode.board.toString());
            // StdOut.println();
            if(mainNode.board.isGoal()){
                this.isSolvable = true;
                this.root = mainNode;
                break;
            }
            else if(twinNode.board.isGoal()) break; // will keep that this is not solvable
            // If we haven't found a solution, add to queue
            else {
                for(Board neighbor : mainNode.board.neighbors()){
                    if(mainNode.previousNode == null) {
                        mainMinPQ.insert(new SearchNode(neighbor, mainNode.moves + 1, mainNode));
                    }
                    else if(!neighbor.equals(mainNode.previousNode.board)){
                        // only add to PQ if board does not match the grandparent (weird terminology, but it makes sense in context)
                        mainMinPQ.insert(new SearchNode(neighbor, mainNode.moves + 1, mainNode));
                    }
                }

                for(Board neighbor : twinNode.board.neighbors()){
                    if(twinNode.previousNode == null) {
                        twinMinPQ.insert(new SearchNode(neighbor, twinNode.moves + 1, twinNode));
                    }
                    else if(!neighbor.equals(twinNode.previousNode.board)){
                        // only add to PQ if board does not match the grandparent (weird terminology, but it makes sense in context)
                        twinMinPQ.insert(new SearchNode(neighbor, twinNode.moves + 1, twinNode));
                    }
                }
            }

        }






    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves(){
        if(isSolvable()){
            return this.root.moves;
        }
        else{
            return -1;
        }
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution(){
        Stack<Board> solutionStack = new Stack<>();
        if (!isSolvable()){
            return null;
        }
        else{
            SearchNode current = root;
            while(current != null){
                solutionStack.push(current.board);
                current = current.previousNode;
                // It seems really weird to me that you can do this... I don't think you can do this in C++?
                // I guess because the memory isn't being reclaimed, you can keep getting the previous nodes
                // even though the objects aren't stored anywhere in the actual code...
            }

        }
        return solutionStack;
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}