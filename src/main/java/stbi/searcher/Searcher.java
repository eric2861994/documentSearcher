package stbi.searcher;

import stbi.common.Document;
import stbi.common.TermFrequency;
import stbi.common.TermFrequencyWeighter;
import stbi.common.TermWeighter;
import stbi.common.term.StringTermStream;

import java.util.List;

/**
 * Handles searching query
 */
public class Searcher {
    private TermWeighter termWeighter;

    Searcher() {
        termWeighter = new TermFrequencyWeighter();
    }

    List<Document> search(String query) {
//        Make Term Stream from the query.
        StringTermStream stringTermStream = new StringTermStream(query);

//        Calculate Term Frequency from the Term Stream.
        TermFrequency termFrequency = new TermFrequency(stringTermStream);

        /* construct vector space model for query

        Determine what weighter to use in calculating the weight for each term in query.
        Convert the query to vector space model.
         */

        // for each document we compute the similarity of it with query
        // return documents sorted descending by similarity, with similarity == 0 discarded
        return null;
    }
}
