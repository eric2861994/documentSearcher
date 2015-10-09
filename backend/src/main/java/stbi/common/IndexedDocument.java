package stbi.common;

import stbi.common.term.Term;
import stbi.indexer.RawDocument;
import stbi.searcher.QueryVector;

import java.util.Map;

/**
 * The indexed document type.
 * <p/>
 * Indexed document contains the original document and the weight of each term in the document.
 * TODO what if the original document is very big? this will cause index to be big!
 */
public class IndexedDocument {
    // contains weight For Each Term in document
    private final RawDocument rawDocument;
    private final Map<Term, Double> termWeight;

    public IndexedDocument(RawDocument _rawDocument, Map<Term, Double> _termWeight) {
        rawDocument = _rawDocument;
        termWeight = _termWeight;
    }

    public double getSimilarity(QueryVector queryVector) {
        return 0;
    }
}
