package stbi.common;

import stbi.common.term.Term;
import stbi.common.term.TermStream;

import java.util.HashMap;
import java.util.Map;

/**
 * Store the frequency of each term.
 */
public class TermFrequency {
    private Map<Term, Integer> wordFrequency = new HashMap<>();

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

    int getFrequency(Term term) {
        return wordFrequency.get(term);
    }

    Term[] getTerms() {
        return (Term[]) wordFrequency.keySet().toArray();
    }
}
