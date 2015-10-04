package stbi.common.index;

import stbi.common.IndexedDocument;
import stbi.common.term.Term;

/**
 * Index interface. The implementation can vary.
 */
public interface Index {
    IndexedDocument[] getDocuments(Term[] terms);
}
