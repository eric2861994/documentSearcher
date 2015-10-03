package stbi.common.index;

import stbi.common.Document;
import stbi.common.term.Term;

/**
 * Modified Inverted Index.
 *
 * Contains both inverted index and normal index.
 */
public class ModifiedInvertedIndex implements Index {

    @Override
    public Document[] getDocuments(Term[] terms) {
        return new Document[0];
    }
}
