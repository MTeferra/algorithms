/**
 * The semantic relatedness of two wordnet nouns A and B as follows:
 * <p>
 * <ul>
 * <p><li><tt>distance(A, B) = </tt>distance is the minimum length of any 
 * ancestral path between any synset v of A and any synset w of B. 
 * </ul>
 * <p>
 * <tt>Outcast Detection.</tt>Given a list of wordnet nouns A1, A2, ..., An, 
 * which noun is the least related to the others? To identify 
 * <tt>an outcast</tt>, compute the sum of the distances between each noun and
 * every other one: 
 * <p>
 *     <tt>di</tt>  =   dist(Ai, A1)   +   dist(Ai, A2)   +   ...   +   
 * dist(Ai, An) 
 * <p>
 * and return a noun At for which dt is maximum. 
 */

public class Outcast {
    
    private final WordNet wn;
    
    private class OutcastElem implements Comparable<OutcastElem> {
        private int dist;
        private String word;
        
        public OutcastElem(String word) {
            this.dist = 0;
            this.word = word;
        }
        
        public int compareTo(OutcastElem other) {
            if (this.dist < other.dist)      return -1;
            else if (this.dist > other.dist) return +1;
            else                             return 0;
        }
    }
    
    /**
     * constructor takes a WordNet object
     */
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new NullPointerException();
        this.wn = wordnet;
    }
    
    /**
     * given an array of WordNet nouns, return an outcast
     * @throws <tt>java.lang.NullPointerException</tt> if argument is null
     * @throws <tt>java.lang.IllegalArgumentException</tt> if any noun
     * is not in the wordnet.
     */
    public String outcast(String[] nouns) {
        if (nouns == null) throw new NullPointerException();
        for (String noun: nouns) {
            if (!wn.isNoun(noun)) throw new IllegalArgumentException();
        }
        
        MaxPQ<OutcastElem> pq = new MaxPQ<OutcastElem>();
        
        for (int i = 0; i < nouns.length; i++) {
            OutcastElem elem = new OutcastElem(nouns[i]);
            for (int j = 0; j < nouns.length; j++) {
                if (i == j) continue;
                elem.dist += wn.distance(nouns[i], nouns[j]);
            }
            pq.insert(elem);
        }
        if (pq.isEmpty()) return null;
        
        return pq.delMax().word;
    }
    
    /**
     * <tt>Test Client. </tt>
     */
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
