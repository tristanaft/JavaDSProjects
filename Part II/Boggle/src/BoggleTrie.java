import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleTrie {

    private static final int R = 'Z' - 'A' + 1;
    private Node root;

    private static class Node {
        private boolean inDict;
        private Node[] next = new Node[R];
    }

    public BoggleTrie(String[] dictionary){
        root = new Node();
        for(String word : dictionary){
            put(word);
        }
    }

    // mostly copied from lecture here
    // only difference is that the value is just true or false
    public void put(String word) {
        root = put(root, word, 0);
    }

    private Node put(Node x, String word, int d) {
        if (x == null) {
            x = new Node();
        }

        if (d == word.length()) {
            x.inDict = true;
            return x;
        }

        char c = word.charAt(d);
        x.next[c-'A'] = put(x.next[c-'A'], word, d+1);
        return x;
    }

    public boolean get(String word) {
        Node x = get(root, word, 0);
        if (x == null) {
            return false;
        }
        return x.inDict;
    }

    public boolean isPrefix(String word) {
        // if the x.next array is empty, there are no valid continuations beyond WORD,
        // so stop adding additional letters

        Node x = get(root, word, 0);
        if (x == null) {
            return false;
        }
        for(Node n : x.next){
            if(n != null){
                // there is at least one letter that continues this
                return true;
            }
        }
        return false;

    }

    private Node get(Node x, String word, int d) {
        if (x == null) return null;
        if (d == word.length()) return x;
        char c = word.charAt(d);
        return get(x.next[c-'A'], word, d+1);
    }


    public static void main(String[] args) {
        //In in = new In(args[0]);]
        In in = new In("dictionary-common.txt");
        String[] dictionary = in.readAllStrings();

        BoggleTrie trie = new BoggleTrie(dictionary);

        String testWord = dictionary[1];

        StdOut.println("Test word is: " + testWord);
        String resp1 = trie.get(testWord) ? "yes" : "no";
        StdOut.println("Is test word in trie?: " + resp1);
        String resp2 = trie.isPrefix(testWord) ? "yes" : "no";
        StdOut.println("Is test word a prefix? " + resp2);

    }

}
