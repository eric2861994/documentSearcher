package stbi.feedback;

import stbi.common.term.Term;
import stbi.indexer.RawDocument;
import stbi.relevance.RelevanceJudge;

import java.util.*;

/**
 * Created by kevinyu on 11/22/15.
 */
public class RelevanceFeedbackUtils {

    public static Map<Term,Double> rochioQueryReweighting(Map<Term,Double> oldQueryVector,
                                           List<RawDocument> relevantDocuments,
                                           List<RawDocument> unrelevantDocuments,
                                           boolean doExapnsion) {
            return constructNewQuery(oldQueryVector,
                    relevantDocuments,
                    unrelevantDocuments,
                    true,
                    doExapnsion);
    }

    public static Map<Term,Double> ideQueryReweighting(Map<Term,Double> oldQueryVector,
                                                        List<RawDocument> relevantDocuments,
                                                        List<RawDocument> unrelevantDocuments,
                                                        boolean doExapnsion) {
        return constructNewQuery(oldQueryVector,
                relevantDocuments,
                unrelevantDocuments,
                false,
                doExapnsion);
    }

    public static Map<Term,Double> deHaiQueryReweighting(Map<Term,Double> oldQueryVector,
                                                         List<RawDocument> relevantDocuments,
                                                         RawDocument unrelevantDocument,
                                                         boolean doExapnsion) {
        List<RawDocument> unrelevantDocuments = new ArrayList<RawDocument>();
        unrelevantDocuments.add(unrelevantDocument);
        return constructNewQuery(oldQueryVector,
                relevantDocuments,
                unrelevantDocuments,
                false,
                doExapnsion);
    }

    public static Map<Term,Double> constructNewQuery(Map<Term,Double> oldQueryVector,
                                                         List<RawDocument> relevantDocuments,
                                                         List<RawDocument> unrelevantDocuments,
                                                        boolean doDivide,
                                                         boolean doExpansions) {

        Map<Term,Double> relevantDocumentVector = constructVectorFromDocuments(relevantDocuments);
        Map<Term,Double> unrelevantDocumentVector = constructVectorFromDocuments(unrelevantDocuments);

        int nRelevantDocuments = relevantDocuments.size();
        int nUnrelevantDocuments = unrelevantDocuments.size();
        if (doDivide) {
            for (Map.Entry<Term,Double> entry : relevantDocumentVector.entrySet()) {
                Term key = entry.getKey();
                relevantDocumentVector.put(key,entry.getValue()/nRelevantDocuments);
            }

            for (Map.Entry<Term,Double> entry : relevantDocumentVector.entrySet()) {
                Term key = entry.getKey();
                unrelevantDocumentVector.put(key,entry.getValue()/nUnrelevantDocuments);
            }
        }

        return computeTermReweighting(oldQueryVector,relevantDocumentVector,unrelevantDocumentVector,doExpansions);
    }

    private static Map<Term,Double> constructVectorFromString(String string) {
        Map<Term,Double> vector = new HashMap<>();
        String[] words = string.split(" ");
        for (String word : words) {
            Term term = new Term(word);
            if (!vector.containsKey(term)) vector.put(term,0.0);
            Double oldWeight = vector.get(term);
            vector.put(term,oldWeight+1);
        }

        return vector;
    }

    private static Map<Term,Double> constructVectorFromDocuments(List<RawDocument> documents) {
        Map<Term,Double> vector = new HashMap<>();
        for (RawDocument document : documents) {
            String[] words = document.getBody().split(" ");
            for (String word : words) {
                Term term = new Term(word);
                if (!vector.containsKey(term)) vector.put(term, 0.0);
                Double oldWeight = vector.get(term);
                vector.put(term, oldWeight + 1);
            }
        }

        return vector;
    }

    public static Map<Term,Double> computeTermReweighting(Map<Term,Double> queryVector,
                                                        Map<Term,Double> relevantVector,
                                                        Map<Term,Double> unrelevantVector,
                                                           boolean doExpansion) {
        Map<Term,Double> resultVector = new HashMap<>();

        Set<Term> resultTerms = relevantVector.keySet();
        if (doExpansion) {
            resultTerms.addAll(relevantVector.keySet());
        }

        Iterator<Term> iterator = resultTerms.iterator();
        while (iterator.hasNext()) {
            Term term = iterator.next();
            Double weight = 0.0;
            if (queryVector.containsKey(term)) weight += queryVector.get(term);
            if (relevantVector.containsKey(term)) weight += relevantVector.get(term);
            if (unrelevantVector.containsKey(term)) weight -= unrelevantVector.get(term);

            if (weight > 0) {
                resultVector.put(term,weight);
            }
        }

        return resultVector;
    }
}
