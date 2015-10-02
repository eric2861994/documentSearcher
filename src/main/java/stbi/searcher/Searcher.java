package stbi.searcher;

import stbi.common.Document;
import stbi.common.term.StringTermStream;

import java.util.List;

/**
 * Handles searching query
 */
public class Searcher {
    List<Document> search(String query) {
//        Make Term Stream from the query.
        StringTermStream stringTermStream = new StringTermStream(query);

        /* construct vector space model for query
        Find what term exists in a query. Create TermFrequency for this term.
        determine what weighter used to calculate the weight for each term in query.
         */

        // for each document we compute the similarity of it with query
        // return documents sorted descending by similarity, with similarity == 0 discarded
        return null;
    }
}
