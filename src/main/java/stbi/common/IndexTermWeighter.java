package stbi.common;

import stbi.common.term.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Weighter for a term in index consisting many documents.
 */
public class IndexTermWeighter {
    // weight is inside document, so just return all weighted document.
    IndexedDocument[] weightAllDocuments(TermWeighter termWeighter, boolean useIDF, boolean useNormalization, TermFrequency[] termFrequencies) {
        // calculate weight using tf
        List<Map<Term, Double>> documentWeightList = new ArrayList<>();
        for (TermFrequency oneTermFrequency : termFrequencies) {
            Map<Term, Double> documentWeight = termWeighter.getTermsWeight(oneTermFrequency);
            documentWeightList.add(documentWeight);
        }

        // consider idf
        if (useIDF) {
            // find term occurrence
            Map<Term, Integer> termOccurrence = new HashMap<>();
            for (Map<Term, Double> documentWeight : documentWeightList) {
                for (Term term : documentWeight.keySet()) {
                    int currentOccurrence = 1;
                    if (termOccurrence.containsKey(term)) {
                        currentOccurrence = termOccurrence.get(term) + 1;
                    }

                    termOccurrence.put(term, currentOccurrence);
                }
            }

            // redefine the weight of each term
            for (Map<Term, Double> documentWeight : documentWeightList) {
                for (Term term : documentWeight.keySet()) {
                    double previousWeight = documentWeight.get(term);
                    documentWeight.put(term, previousWeight * termIDF(termOccurrence.get(term), documentWeightList.size()));
                }
            }
        }

        // consider normalization
        if (useNormalization) {

        }
        return null;
    }

    /**
     * Calculate the IDF value for a term.
     *
     * Using the standard IDF formula log(N/n_t).
     *
     * @param documentOccurrence    the number of documents containing a specific term
     * @param documentsCount        the total number of documents
     * @return IDF
     */
    private double termIDF(int documentOccurrence, int documentsCount) {
        return Math.log((double)documentsCount/documentOccurrence);
    }
}
