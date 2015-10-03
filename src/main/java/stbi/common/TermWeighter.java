package stbi.common;

import stbi.common.term.Term;

import java.util.Map;

/**
 * Weighter for a term.
 */
public interface TermWeighter {
    Map<Term, Double> getTermsWeight(TermFrequency termFrequency);
}
