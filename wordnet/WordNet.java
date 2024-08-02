
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class WordNet {

    // need a dictionary to go from a string to a synset index.
    // Each int is associated with one word, but each word can have many indices...
    private HashMap<String, List<Integer>> stringToId;
    private HashMap<Integer, String> idToString;
    private Digraph wordDigraph;
    private SAP sap;



    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if(synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("Nulls passed to WordNet");
        }
        // Load synsets
        In synFile = new In(synsets);
        this.stringToId = new HashMap<>();
        this.idToString = new HashMap<>();

        String[] allLines = synFile.readAllLines();
        this.wordDigraph = new Digraph(allLines.length);
        for(String line : allLines) {
            String[] lineArr = line.split(",");
            int id = Integer.parseInt(lineArr[0]);
            List<Integer> idArr = new ArrayList<>();
            String[] synset = lineArr[1].split(" ");
            for(String word : synset) {
                this.idToString.put(id, word);
                if(this.stringToId.containsKey(word)){
                    idArr = this.stringToId.get(word);
                }
                idArr.add(id);
                this.stringToId.put(word, idArr);
            }
        }
        synFile.close();

        In hyperFile = new In(hypernyms);
        String[] allLines2 = hyperFile.readAllLines();
        for(String line : allLines2) {
            String[] lineArr = line.split(",");
            // first is the index
            int idx = Integer.parseInt(lineArr[0]);
            // the rest are the outgoing connections
            for(int i = 1; i < lineArr.length; ++i) {
                this.wordDigraph.addEdge(idx, Integer.parseInt(lineArr[i]));
            }
        }
        hyperFile.close();
        this.sap = new SAP(wordDigraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return this.idToString.values();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return this.stringToId.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if(isNoun(nounA) && isNoun(nounB)) {
            List<Integer> v = stringToId.get(nounA);
            List<Integer> w = stringToId.get(nounB);
            // need to pass iterables
            return sap.length(v, w);
        }
        else {
            throw new IllegalArgumentException("Nouns do not exist in wordnet");
        }
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        List<Integer> v = stringToId.get(nounA);
        List<Integer> w = stringToId.get(nounB);
        // need to pass iterables
        int id = sap.ancestor(v, w);
        return idToString.get(id);
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}
