package stbi.searcher;

import stbi.common.*;
import stbi.common.index.Index;
import stbi.common.index.ModifiedInvertedIndex;
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

    private TermWeighter termWeighter;

    Searcher() {
        termWeighter = new TermFrequencyWeighter();
        index = new ModifiedInvertedIndex();
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

//        Find the documents that contain the query.
        Document[] documents = index.getDocuments(new Term[0]);

        // for each document we compute the similarity of it with query
        DocumentSimilarity[] documentSimilarityArray = new DocumentSimilarity[documents.length];
        int idx = 0;
        for (Document oneDocument : documents) {
            double similarity = oneDocument.getSimilarity(queryVector);
            documentSimilarityArray[idx] = new DocumentSimilarity(similarity, oneDocument);

            idx++;
        }

        // sort documents descending by similarity
        Arrays.sort(documentSimilarityArray);

        // select documents with similarity > 0
        List<DocumentSimilarity> selectedDocumentSimilarity = new ArrayList<>();
        for (DocumentSimilarity oneDocumentSimilarity : documentSimilarityArray) {
            if (oneDocumentSimilarity.getSimilarity() > 0) {
                selectedDocumentSimilarity.add(oneDocumentSimilarity);
            }
        }

        return selectedDocumentSimilarity;
    }
}
