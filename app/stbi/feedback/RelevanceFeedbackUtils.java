package stbi.feedback;

import stbi.common.term.Term;
import stbi.indexer.RawDocument;
import stbi.relevance.RelevanceJudge;

import java.util.*;

/**
 * Created by kevinyu on 11/22/15.
 */
public class RelevanceFeedbackUtils {

    //use for Rochio, Ide - dechi , and De - hai
    public static Map<Term,Integer> constructNewQuery(Map<Term,Integer> oldQueryVector,
                                                         List<RawDocument> relevantDocuments,
                                                         List<RawDocument> unrelevantDocuments,
                                                         boolean doExpansions) {

        Map<Term,Integer> relevantDocumentVector = constructVectorFromDocuments(relevantDocuments);
        Map<Term,Integer> unrelevantDocumentVector = constructVectorFromDocuments(unrelevantDocuments);

        return computeTermReweighting(oldQueryVector,relevantDocumentVector,unrelevantDocumentVector,doExpansions);
    }

    private static Map<Term,Integer> constructVectorFromString(String string) {
        Map<Term,Integer> vector = new HashMap<>();
        String[] words = string.split(" ");
        for (String word : words) {
            Term term = new Term(word);
            if (!vector.containsKey(term)) vector.put(term,0);
            Integer oldWeight = vector.get(term);
            vector.put(term,oldWeight+1);
        }

        return vector;
    }

    private static Map<Term,Integer> constructVectorFromDocuments(List<RawDocument> documents) {
        Map<Term,Integer> vector = new HashMap<>();
        for (RawDocument document : documents) {
            String[] words = document.getBody().split(" ");
            for (String word : words) {
                Term term = new Term(word);
                if (!vector.containsKey(term)) vector.put(term, 0);
                Integer oldWeight = vector.get(term);
                vector.put(term, oldWeight + 1);
            }
        }

        return vector;
    }

    public static Map<Term,Integer> computeTermReweighting(Map<Term,Integer> queryVector,
                                                        Map<Term,Integer> relevantVector,
                                                        Map<Term,Integer> unrelevantVector,
                                                           boolean doExpansion) {
        Map<Term,Integer> resultVector = new HashMap<>();

        Set<Term> resultTerms = relevantVector.keySet();
        if (doExpansion) {
            resultTerms.addAll(relevantVector.keySet());
        }

        Iterator<Term> iterator = resultTerms.iterator();
        while (iterator.hasNext()) {
            Term term = iterator.next();
            Integer weight = 0;
            if (queryVector.containsKey(term)) weight += queryVector.get(term);
            if (relevantVector.containsKey(term)) weight += relevantVector.get(term);
            if (unrelevantVector.containsKey(term)) weight -= unrelevantVector.get(term);

            if (weight > 0) {
                queryVector.put(term,weight);
            }
        }

        return resultVector;
    }
}
