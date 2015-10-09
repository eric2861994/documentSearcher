package stbi.common;

import stbi.common.term.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Weighter for a term in index consisting many documents.
 * <p/>
 * This class is separated from ModifiedInvertedIndexer because it might be reused in other Indexer.
 */
public class IndexTermWeighter {

    public List<Map<Term, Double>> weightAllDocuments(
            TermWeighter termWeighter, boolean useIDF, boolean useNormalization, TermFrequency[] termFrequencies,
            Map<Term, List<Integer>> termDocument) {
        // calculate weight using tf
        List<Map<Term, Double>> documentWeightList = new ArrayList<>();
        for (TermFrequency oneTermFrequency : termFrequencies) {
            Map<Term, Double> documentWeight = termWeighter.getTermsWeight(oneTermFrequency);
            documentWeightList.add(documentWeight);
        }

        // consider idf
        if (useIDF) {
            // redefine the weight of each term
            for (Map<Term, Double> documentWeight : documentWeightList) {
                for (Term term : documentWeight.keySet()) {
                    double previousWeight = documentWeight.get(term);
                    documentWeight.put(term, previousWeight * termIDF(termDocument.get(term).size(), documentWeightList.size()));
                }
            }
        }

        // consider normalization
        if (useNormalization) {
            // calculate length of each document
            double[] documentLength = new double[termFrequencies.length];
            for (int docIdx = 0; docIdx < termFrequencies.length; docIdx++) {
                double oneDocumentLength = 0;
                Map<Term, Double> documentWeight = documentWeightList.get(docIdx);
                for (Term term : documentWeight.keySet()) {
                    oneDocumentLength += documentWeight.get(term) * documentWeight.get(term);
                }
                documentLength[docIdx] = Math.sqrt(oneDocumentLength);
            }

            // divide every term weight of a document by the document's length
            for (int docIdx = 0; docIdx < termFrequencies.length; docIdx++) {
                Map<Term, Double> documentWeight = documentWeightList.get(docIdx);
                for (Term term : documentWeight.keySet()) {
                    double previousWeight = documentWeight.get(term);
                    documentWeight.put(term, previousWeight / documentLength[docIdx]);
                }
            }
        }

        return documentWeightList;
    }

    /**
     * Calculate the IDF value for a term.
     * <p/>
     * Using the standard IDF formula log(N/n_t).
     *
     * @param documentOccurrence the number of documents containing a specific term
     * @param documentsCount     the total number of documents
     * @return IDF
     */
    private double termIDF(int documentOccurrence, int documentsCount) {
        return Math.log((double) documentsCount / documentOccurrence);
    }
}
