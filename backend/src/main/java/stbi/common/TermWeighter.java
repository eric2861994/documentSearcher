package stbi.common;

import stbi.common.term.Term;

import java.util.Map;

/**
 * Weighter for a term in a single query or a document.
 *
 * Implementations: TF, binary TF, log TF, normalized TF
 */
public interface TermWeighter {
    Map<Term, Double> getTermsWeight(TermFrequency termFrequency);
}
