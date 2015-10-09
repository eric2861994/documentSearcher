package stbi.searcher;

import stbi.common.*;
import stbi.common.index.Index;
import stbi.common.term.StringTermStream;
import stbi.common.term.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Handles searching query
 */
public class Searcher {
    private final Index index;

    private TermWeighter termWeighter = new TermFrequencyWeighter();

    Searcher(Index _index) {
        index = _index;
    }

    List<DocumentSimilarity> search(String query) {
//        Make Term Stream from the query.
        StringTermStream stringTermStream = new StringTermStream(query);

//        Calculate Term Frequency from the Term Stream.
        TermFrequency termFrequency = new TermFrequency(stringTermStream);

        Map<Term, Double> termsWeight = termWeighter.getTermsWeight(termFrequency);

        /* construct vector space model for query
        Convert the query to vector space model.
         */

        QueryVector queryVector = null;

//        Find the indexedDocuments that contain the query.
        IndexedDocument[] indexedDocuments = index.getDocuments(new Term[0]);

        // for each document we compute the similarity of it with query
        DocumentSimilarity[] documentSimilarityArray = new DocumentSimilarity[indexedDocuments.length];
        int idx = 0;
        for (IndexedDocument oneIndexedDocument : indexedDocuments) {
            // TODO this might not need queryvector, supply Map<Term, Double> instead.
            double similarity = oneIndexedDocument.getSimilarity(queryVector);
            documentSimilarityArray[idx] = new DocumentSimilarity(similarity, oneIndexedDocument);

            idx++;
        }

        // sort indexedDocuments descending by similarity
        Arrays.sort(documentSimilarityArray);

        // select indexedDocuments with similarity > 0
        List<DocumentSimilarity> selectedDocumentSimilarity = new ArrayList<>();
        for (DocumentSimilarity oneDocumentSimilarity : documentSimilarityArray) {
            if (oneDocumentSimilarity.getSimilarity() > 0) {
                selectedDocumentSimilarity.add(oneDocumentSimilarity);
            }
        }

        return selectedDocumentSimilarity;
    }
}
