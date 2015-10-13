package stbi.common.index;

import stbi.common.IndexedDocument;
import stbi.common.term.Term;
import stbi.common.util.Pair;
import stbi.searcher.QueryVector;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Modified Inverted Index.
 * <p/>
 * Contains inverted index and indexed documents.
 * TODO implement save and load
 */
public class ModifiedInvertedIndex implements Index, Serializable {
    private final Map<Term, List<Pair<Integer, Double>>> termDocumentWeight;
    private final IndexedDocument[] indexedDocuments;

    public ModifiedInvertedIndex(Map<Term, List<Pair<Integer, Double>>> _termDocumentWeight, IndexedDocument[] _indexedDocuments) {
        termDocumentWeight = _termDocumentWeight;
        indexedDocuments = _indexedDocuments;
    }

    public void save(File indexFIle) {
        // TODO impl
    }

    public void load(File indexFile) {
        // TODO impl
    }

    @Override
    public IndexedDocument[] getDocuments(Term[] terms) {
        return new IndexedDocument[0];
    }

    @Override
    public int getAllDocumentCount() {
        return indexedDocuments.length;
    }

    @Override
    public int getDocumentCount(Term term) {
        return termDocumentWeight.get(term).size();
    }

    @Override
    public double getSimilarity(QueryVector query, IndexedDocument document) {
        return 0;
    }
}
