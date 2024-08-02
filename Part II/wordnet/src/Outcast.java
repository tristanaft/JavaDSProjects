import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    final private WordNet wordNet;
    public Outcast(WordNet wordnet)         // constructor takes a WordNet object
    {this.wordNet = wordnet;}
    public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
    {   String out = "" ;
        int longestDist = -1;
        for (String s1 : nouns) {
            int totalDist = 0;
            for (String s2 : nouns) {
                // note, the distance from an object to itself is 0
                totalDist += wordNet.distance(s1, s2);
            }
            if (totalDist > longestDist) {
                longestDist = totalDist;
                out = s1;
            }
    }
    return out;
    }
    public static void main(String[] args)  // see test client below
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}