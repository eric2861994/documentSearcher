package stbi.common.index;

import stbi.common.IndexedDocument;
import stbi.common.term.Term;
import stbi.common.util.Pair;
import stbi.searcher.QueryVector;

import java.util.List;
import java.util.Map;

/**
 * Modified Inverted Index.
 * <p/>
 * Contains both inverted index and normal index.
 * TODO might have to be serializable to be able to save to file easily.
 */
public class ModifiedInvertedIndex implements Index {
    private final Map<Term, Pair<Integer, Double>> termDocumentWeight;
    private final IndexedDocument[] indexedDocuments;

    public ModifiedInvertedIndex(Map<Term, Pair<Integer, Double>> _termDocumentWeight, IndexedDocument[] _indexedDocuments) {
        termDocumentWeight = _termDocumentWeight;
        indexedDocuments = _indexedDocuments;
    }

    @Override
    public IndexedDocument[] getDocuments(Term[] terms) {
        return new IndexedDocument[0];
    }

    @Override
    public int getAllDocumentCount() {
        return 0;
    }

    @Override
    public int getDocumentCount(Term term) {
        return 0;
    }

    @Override
    public double getSimilarity(QueryVector query, IndexedDocument document) {
        return 0;
    }
}
