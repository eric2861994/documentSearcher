package stbi.common;

import stbi.common.term.Term;

import java.util.HashMap;
import java.util.Map;

/**
 * Term Frequency Weighter. Use the frequency as the weight.
 */
public class TermFrequencyWeighter implements TermWeighter {

    @Override
    public Map<Term, Double> getTermsWeight(TermFrequency termFrequency) {
        Map<Term, Double> termsWeight = new HashMap<>();

        for (Term term : termFrequency.getTerms()) {
            termsWeight.put(term, (double) termFrequency.getFrequency(term));
        }

        return termsWeight;
    }
}
