import java.util.List;
import java.util.LinkedList;

/**
 * WordNet groups words into sets of synonyms called synsets and describes 
 * semantic relationships between them. One such relationship is the is-a 
 * relationship, which connects a hyponym (more specific synset) to a 
 * hypernym (more general synset). For example, a plant organ is a hypernym
 * of carrot and plant organ is a hypernym of plant root. 
 * <p>
 * The WordNet class builds the wordnet digraph: each vertex v is an integer
 * that represents a synset, and each directed edge v?w represents that w is
 * a hypernym of v. The wordnet digraph is a rooted DAG: it is acyclic and has
 * one <tt>vertex-the-root</tt> that is an ancestor of every other vertex. 
 * However, it is not necessarily a tree because a synset can have more 
 * than one hypernym. 
 * <p>
 * <tt>Performance Requirements. </tt>Space used should belinear in the input 
 * size (size of synsets and hypernyms files). The constructor should take time 
 * linearithmic (or better) in the input size. The method <tt>isNoun() </tt>
 * should run in time logarithmic (or better) in the number of nouns. 
 * The methods <tt>distance()</tt> and <tt>sap()</tt> should run in time linear
 * in the size of the WordNet digraph. For the analysis, assume that the number
 * of nouns per synset is bounded by a constant. 
 */

public class WordNet {
    
    private final ST<String, List<Integer>> synsetNouns;
    private final ST<Integer, String> synsetIds;
    
    private final Digraph dg;
    private final SAP     sap;
    
    /**
     * constructor takes the name of the two input files
     * @param synsets name of the synsets input file
     * #param hypernym name of the hypernym input file
     * @throws <tt>java.lang.NullPointerException</tt> if either argument is 
     * null
     * @throws <tt>java.lang.IllegalArgumentException</tt> if the input does 
     * not correspond to a rooted DAG
     */
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null)   throw new NullPointerException();
        if (hypernyms == null) throw new NullPointerException();
 
        this.synsetNouns = new ST<String, List<Integer>>();    
        this.synsetIds   = new ST<Integer, String>();
        readSynsets(synsets);
        
        this.dg = new Digraph(this.synsetNouns.size());
        readHypernyms(hypernyms);
        if (!isRootedDAG()) throw new IllegalArgumentException();
        sap = new SAP(dg);
    }
    
    /**
     * returns all WordNet nouns
     */
    public Iterable<String> nouns() {
        return synsetNouns.keys();
    }
    
    /**
     * is the word a WordNet noun?
     * @throws <tt>java.lang.NullPointerException</tt> if word is null
     */
    public boolean isNoun(String word) {
        if (word == null) throw new NullPointerException();
        return synsetNouns.contains(word);
    }
    
    /**
     * distance between nounA and nounB (defined below)
     * @throws <tt>java.lang.NullPointerException</tt> if arguments are null
     * @throws <tt>java.lang.IllegalArgumentException</tt> if arguments illegal
     */
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new NullPointerException();
        if (!isNoun(nounA) || !isNoun(nounB)) 
            throw new IllegalArgumentException();
        
        List<Integer> a = synsetNouns.get(nounA);
        List<Integer> b = synsetNouns.get(nounB);
        
        return sap.length(a, b);
    }
    
    /**
     * A synset (second field of synsets.txt) that is the common ancestor of 
     * nounA and nounB in a shortest ancestral path (defined below)
     */
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new NullPointerException();
        if (!isNoun(nounA) || !isNoun(nounB)) 
            throw new IllegalArgumentException();
        
        List<Integer> a = synsetNouns.get(nounA);
        List<Integer> b = synsetNouns.get(nounB);
        int ancestor = sap.ancestor(a, b);
        if (ancestor == -1) return null;
        
        return synsetIds.get(ancestor);
    }
    
    private void readSynsets(String synsets) {
        In in = new In(synsets);
        
        while  (in.hasNextLine()) {
            String line = in.readLine();
            String[] tokens = line.split(",");
            int id = Integer.parseInt(tokens[0]);
            synsetIds.put(id, tokens[1]);
            
            String[] nouns = tokens[1].split(" ");
            for (String noun: nouns) {
                if (!synsetNouns.contains(noun)) {
                    List<Integer> ids = new LinkedList<Integer>();
                    ids.add(id);
                    synsetNouns.put(noun, ids);
                } else {
                    List<Integer> ids = synsetNouns.get(noun);
                    ids.add(id);
                }
            }
            
        }
    }
    
    private void readHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] tokens = line.split(",");
            int v = Integer.parseInt(tokens[0]);
            
            for (int i = 1; i < tokens.length; i++) {
                int w = Integer.parseInt(tokens[i]);
                dg.addEdge(v, w);
            }
        }
    }
    
    /**
     * A Digraph has a topological order iff no directed cycle.
     */
    private boolean isRootedDAG() {
        DirectedCycle dc = new DirectedCycle(this.dg);
        if (dc.hasCycle()) return false;
        
        Topological t = new Topological(this.dg);
        return t.hasOrder();
    }   
    
    /**
     * do unit testing of this class .The data files are in CSV format: 
     * each line contains a sequence of fields, separated by commas. 
     * <p>
     * <ul>
     * <p><li><tt>List of noun-sets.</tt> The file <tt>synsets.txt</tt>
     * ists all the (noun) synsets in WordNet. The first field is the 
     * <tt>synset id</tt>(an integer), the second field is the synonym set (or 
     * <tt>synset</tt>) and the third field is its dictionary definition (or 
     * <tt>gloss</tt>.For example, the line 
     * <p>
     *        36,AND_circuit AND_gate,a circuit in a computer that fires only
     * when all of its inputs fire
     * <p>
     * means that the synset { AND_circuit, AND_gate } has an id number of 36 
     * and it's gloss is a circuit in a computer that fires only when all of 
     * its inputs fire. The individual nouns that comprise a synset are 
     * separated by spaces (and a synset element is not permitted to contain a
     * space). The S synset ids are numbered 0 through S ? 1; the id numbers 
     * will appear consecutively in the synset file. 
     * <p><li><tt>List of hypernyms.</tt> The file <tt>hypernyms.txt</tt> 
     * contains the hypernym relationships: The first field is a synset id; 
     * subsequent fields are the id numbers of the synset's hypernyms. 
     * For example, the following line 
     * <p>
     *        164,21012,56099
     * <p>
     * means that the the synset 164 <tt>("Actified")</tt> two hypernyms: 21012
     * <tt>("antihistamine")</tt> and 56099 <tt>("nasal_decongestant")</tt>, 
     * representing that Actifed is both an antihistamine and a 
     * nasal decongestant. The synsets are obtained from the corresponding lines
     * in the file <tt>synsets.txt></tt>. 
     * <p>164,Actifed,trade name for a drug containing an antihistamine 
     * and a decongestant...
     * <p>21012,antihistamine,a medicine used to treat allergies...
     * <p>56099,nasal_decongestant,a decongestant that provides temporary relief
     * of nasal...
     * </ul>
     * 
     */
    public static void main(String[] args) {
    }
}

