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
            case BINARY_TF:
                return getBinaryTf(termFrequency);
            case LOG_TF:
                return getLogTf(termFrequency);
            case AUGMENTED_TF:
                return getAugmentedTf(termFrequency);
            default:
                return null;
        }
    }

    private Map<Term, Double> getRawTF(TermFrequency termFrequency) {
        Map<Term, Double> termsWeight = new HashMap<>();
        for (Term term : termFrequency.getTerms()) {
            termsWeight.put(term, termFrequency.getFrequency(term));
        }

        return termsWeight;
    }

    private Map<Term, Double> getBinaryTf(TermFrequency termFrequency){
        Map<Term, Double> termsWeight = new HashMap<>();
        for(Term term : termFrequency.getTerms()){
            termsWeight.put(term, 1.0);
        }
        return termsWeight;
    }

    private Map<Term, Double> getLogTf(TermFrequency termFrequency){
        Map<Term, Double> termsWeight = new HashMap<>();
        for(Term term : termFrequency.getTerms()){
            termsWeight.put(term, 1.0+Math.log10(termFrequency.getFrequency(term)));
        }
        return termsWeight;
    }

    private Map<Term, Double> getAugmentedTf(TermFrequency termFrequency){
        Map<Term, Double> termsWeight = new HashMap<>();
        double maxTf = 1.0;
        // Get Max Tf
        for(Term term : termFrequency.getTerms()){
            if (termFrequency.getFrequency(term) > maxTf) maxTf = termFrequency.getFrequency(term);
        }
        // Calculate Augmented Tf
        for(Term term : termFrequency.getTerms()){
            termsWeight.put(term, 0.5+0.5*termFrequency.getFrequency(term)/maxTf);
        }
        return termsWeight;
    }

    public enum TFType {
        RAW_TF,
        BINARY_TF,
        LOG_TF,
        AUGMENTED_TF
    }
}
