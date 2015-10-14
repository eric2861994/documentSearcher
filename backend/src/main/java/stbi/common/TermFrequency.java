package stbi.common;

import stbi.common.term.Term;
import stbi.common.term.TermStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Store the frequency of each term.
 */
public class TermFrequency {
    private Map<Term, Integer> wordFrequency = new HashMap<Term, Integer>();

    public TermFrequency(TermStream termStream) {
        while (termStream.hasNext()) {
            Term term = termStream.next();

            int currentFrequency = 1;
            if (wordFrequency.containsKey(term)) {
                currentFrequency = wordFrequency.get(term) + 1;
            }
            wordFrequency.put(term, currentFrequency);
        }
    }

    public double getFrequency(Term term) {
        return wordFrequency.get(term);
    }

    public Term[] getTerms() {
        Set<Term> keyset = wordFrequency.keySet();
        return keyset.toArray(new Term[keyset.size()]);
    }
}
