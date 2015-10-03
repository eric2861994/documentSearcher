package stbi.common.index;

import stbi.common.Document;
import stbi.common.term.Term;

/**
 * Index interface. The implementation can vary.
 */
public interface Index {
    Document[] getDocuments(Term[] terms);
}
