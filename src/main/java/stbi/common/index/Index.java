package stbi.common.index;

import stbi.common.IndexedDocument;
import stbi.common.term.Term;

/**
 * Index interface. The implementation can vary.
 */
public interface Index {
    /**
     * Get all the documents that contains at least one term in terms.
     *
     * @param terms    terms array
     * @return all documents containing at least one term in terms
     */
    IndexedDocument[] getDocuments(Term[] terms);

    /**
     * Get the number of documents in index.
     *
     * @return number of documents in index.
     */
    int getAllDocumentCount();

    /**
     * Get the number of documents of a term in index.
     *
     * @return number of documents of a term in index.
     */
    int getDocumentCount(Term term);
}
