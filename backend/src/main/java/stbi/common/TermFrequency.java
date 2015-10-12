package stbi.common;

import stbi.common.term.Term;
import stbi.common.term.TermStream;

import java.util.HashMap;
import java.util.Map;

/**
 * Store the frequency of each term.
 */
public class TermFrequency {
    private Map<Term, Integer> wordFrequency = new HashMap<Term, Integer>();
    private int totalFrequency;
    private boolean isNormalized;

    public TermFrequency(TermStream termStream) {
        isNormalized = false;
        totalFrequency = 0;

        while (termStream.hasNext()) {
            Term term = termStream.next();
            totalFrequency++;

            int currentFrequency = 1;
            if (wordFrequency.containsKey(term)) {
                currentFrequency = wordFrequency.get(term) + 1;
            }
            wordFrequency.put(term, currentFrequency);
        }
    }

    public double getFrequency(Term term) {
        if(isNormalized){
            return wordFrequency.get(term)/totalFrequency;
        } else{
            return wordFrequency.get(term);
        }
    }

    public Term[] getTerms() {
        return wordFrequency.keySet().toArray(new Term[wordFrequency.keySet().size()]);
    }

    public boolean isNormalized() {
        return isNormalized;
    }

    public void setIsNormalized(boolean isNormalized) {
        this.isNormalized = isNormalized;
    }
}
