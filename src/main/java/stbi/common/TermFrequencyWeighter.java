package stbi.common;

import stbi.common.term.Term;

import java.util.HashMap;
import java.util.Map;

/**
 * Term Frequency Weighter. Use the frequency as the weight.
 */
public class TermFrequencyWeighter extends TermWeighter {

    @Override
    Map<Term, Double> getTermsWeight(TermFrequency termFrequency) {
        Map<Term, Double> termWeight = new HashMap<Term, Double>();

        for (Term term : termFrequency.getTerms()) {
            termWeight.put(term, (double) termFrequency.getFrequency(term));
        }

        return termWeight;
    }
}
