package stbi.common.index;

import stbi.common.IndexedDocument;
import stbi.common.term.Term;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Modified Inverted Index.
 * <p/>
 * Contains inverted index and indexed documents.
 */
public class ModifiedInvertedIndex implements Index, Serializable {
    private final Map<Term, Map<Integer, Double>> termDocumentWeight;
    private final IndexedDocument[] indexedDocuments;

    public ModifiedInvertedIndex(Map<Term, Map<Integer, Double>> _termDocumentWeight, IndexedDocument[] _indexedDocuments) {
        termDocumentWeight = _termDocumentWeight;
        indexedDocuments = _indexedDocuments;
    }

    @Override
    public Integer[] getDocumentIDs(Term[] terms) {
        Set<Integer> idSet = new HashSet<>();
        for (Term term : termDocumentWeight.keySet()) {
            if (termDocumentWeight.containsKey(term)) {
                for (Integer docID : termDocumentWeight.get(term).keySet()) {
                    idSet.add(docID);
                }
            }
        }

        return idSet.toArray(new Integer[idSet.size()]);
    }

    @Override
    public int getAllDocumentCount() {
        return indexedDocuments.length;
    }

    @Override
    public int getDocumentCount(Term term) {
        if (termDocumentWeight.containsKey(term)) {
            return termDocumentWeight.get(term).size();

        } else {
            return 0;
        }
    }

    @Override
    public double getSimilarity(Map<Term, Double> query, Integer docID) {
        double similarity = 0;
        for (Term term : query.keySet()) {
            if (termDocumentWeight.containsKey(term)) {
                Map<Integer, Double> documentWeight = termDocumentWeight.get(term);
                if (documentWeight.containsKey(docID)) {
                    similarity += query.get(term) * documentWeight.get(docID);
                }
            }
        }

        return similarity;
    }

    @Override
    public IndexedDocument getIndexedDocument(int docID) {
        return indexedDocuments[docID];
    }
}
