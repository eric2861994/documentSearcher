package stbi.common;

import stbi.common.term.Term;

import java.util.Map;

/**
 * Weighter for a term.
 */
public abstract class TermWeighter {
    abstract Map<Term, Double> getTermsWeight(TermFrequency termFrequency);
}
