package stbi.common.index;

import stbi.common.IndexedDocument;
import stbi.common.term.Term;

import java.util.List;
import java.util.Map;

/**
 * Index interface. The implementation can vary.
 */
public interface Index {
    /**
     * Get all ids of the document that contains at least one term in terms.
     *
     * @param terms terms array
     * @return all ids of the document that contains at least one term in terms
     */
    Integer[] getDocumentIDs(Term[] terms);

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

    /**
     * Get the similarity of query against document with docID.
     *
     * @param query query
     * @param docID document ID
     * @return similarity
     */
    double getSimilarity(Map<Term, Double> query, Integer docID);

    /**
     * Get indexed document with id docID
     *
     * @param docID document id
     * @return indexed document
     */
    IndexedDocument getIndexedDocument(int docID);

    Map<Term, Double> getDocumentTermVector(Integer docID);
}
