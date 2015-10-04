package stbi.common.index;

import stbi.common.IndexedDocument;
import stbi.common.term.Term;

/**
 * Modified Inverted Index.
 *
 * Contains both inverted index and normal index.
 */
public class ModifiedInvertedIndex implements Index {

    @Override
    public IndexedDocument[] getDocuments(Term[] terms) {
        return new IndexedDocument[0];
    }
}
