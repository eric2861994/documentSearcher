package stbi.common.util;

import stbi.common.TermFrequency;
import stbi.common.term.Term;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for various calculations.
 */
public class Calculator {
    /**
     * Calculate TF Value.
     * @param type             TFType
     * @param termFrequency    TermFrequency
     * @return TF Value
     */
    public Map<Term, Double> getTFValue(TFType type, TermFrequency termFrequency) {
        switch (type) {
            case RAW_TF:
                return getRawTF(termFrequency);
            default:
                return null;
        }
    }

    private Map<Term, Double> getRawTF(TermFrequency termFrequency) {
        Map<Term, Double> termsWeight = new HashMap<>();
        for (Term term : termFrequency.getTerms()) {
            termsWeight.put(term, (double) termFrequency.getFrequency(term));
        }

        return termsWeight;
    }

    public enum TFType {
        RAW_TF
    }
}
