package stbi.common;

import stbi.common.term.Term;
import stbi.searcher.QueryVector;

import java.util.Map;

/**
 * TODO comment
 */
public class IndexedDocument {
    // contains weight For Each Term in document
    private Map<Term, Double> termWeight;

    public double getSimilarity(QueryVector queryVector) {
        return 0;
    }
}
