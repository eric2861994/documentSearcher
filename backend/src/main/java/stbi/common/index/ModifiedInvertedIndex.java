package stbi.common.index;

import stbi.common.IndexedDocument;
import stbi.common.term.Term;

import java.util.List;
import java.util.Map;

/**
 * Modified Inverted Index.
 * <p/>
 * Contains both inverted index and normal index.
 * TODO might have to be serializable to be able to save to file easily.
 */
public class ModifiedInvertedIndex implements Index {
    private final Map<Term, List<Integer>> termDocument;
    private final IndexedDocument[] documents;

    public ModifiedInvertedIndex(Map<Term, List<Integer>> _termDocument, IndexedDocument[] indexedDocuments) {
        termDocument = _termDocument;
        documents = indexedDocuments;
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
}
