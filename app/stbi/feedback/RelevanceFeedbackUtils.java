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
                                           List<Map<Term,Double>> relevantDocuments,
                                           List<Map<Term,Double>> unrelevantDocuments,
                                           boolean doExpansion) {

        if (oldQueryVector == null) oldQueryVector = new HashMap<Term,Double>();
        if (relevantDocuments == null) relevantDocuments = new ArrayList<Map<Term,Double>>();
        if (unrelevantDocuments == null) unrelevantDocuments = new ArrayList<Map<Term,Double>>();

        Map<Term,Double> relevantDocumentVector = addTermVector(relevantDocuments);
        Map<Term,Double> unrelevantDocumentVector = addTermVector(unrelevantDocuments);

        int nRelevantDocuments = relevantDocuments.size();
        int nUnrelevantDocuments = unrelevantDocuments.size();
        for (Map.Entry<Term,Double> entry : relevantDocumentVector.entrySet()) {
            Term key = entry.getKey();
            relevantDocumentVector.put(key,entry.getValue()/nRelevantDocuments);
        }

        for (Map.Entry<Term,Double> entry : unrelevantDocumentVector.entrySet()) {
            Term key = entry.getKey();
            unrelevantDocumentVector.put(key,entry.getValue()/nUnrelevantDocuments);
        }

        return computeTermReweighting(oldQueryVector,
                relevantDocumentVector,
                unrelevantDocumentVector,
                doExpansion);

    }

    public static Map<Term,Double> ideQueryReweighting(Map<Term,Double> oldQueryVector,
                                                        List<Map<Term,Double>> relevantDocuments,
                                                        List<Map<Term,Double>> unrelevantDocuments,
                                                        boolean doExpansion) {

        if (oldQueryVector == null) oldQueryVector = new HashMap<Term,Double>();
        if (relevantDocuments == null) relevantDocuments = new ArrayList<Map<Term,Double>>();
        if (unrelevantDocuments == null) unrelevantDocuments = new ArrayList<Map<Term,Double>>();
        Map<Term,Double> relevantDocumentVector = addTermVector(relevantDocuments);
        Map<Term,Double> unrelevantDocumentVector = addTermVector(unrelevantDocuments);
        return computeTermReweighting(oldQueryVector,
                relevantDocumentVector,
                unrelevantDocumentVector,
                doExpansion);
    }

    public static Map<Term,Double> deHaiQueryReweighting(Map<Term,Double> oldQueryVector,
                                                         List<Map<Term,Double>> relevantDocuments,
                                                         Map<Term,Double> unrelevantDocument,
                                                         boolean doExpansion) {

        if (oldQueryVector == null) oldQueryVector = new HashMap<Term,Double>();
        if (relevantDocuments == null) relevantDocuments = new ArrayList<Map<Term,Double>>();
        if (unrelevantDocument == null) unrelevantDocument = new HashMap<Term,Double>();

        Map<Term,Double> relevantDocumentVector = addTermVector(relevantDocuments);
        return computeTermReweighting(oldQueryVector,
                relevantDocumentVector,
                unrelevantDocument,
                doExpansion);
    }

    private static Map<Term,Double> addTermVector(List<Map<Term,Double>> termVectors) {
        Map<Term,Double> resultVector = new HashMap<>();

        Set<Term> resultTerms = new HashSet<Term>();
        for (Map<Term,Double> termVector : termVectors) {
            resultTerms.addAll(termVector.keySet());
        }

        Iterator<Term> iterator = resultTerms.iterator();
        while(iterator.hasNext()) {
            Term term = iterator.next();
            Double weight = 0.0;
            for (Map<Term,Double> termVector : termVectors) {
                if (termVector.containsKey(term)) weight += termVector.get(term);
            }
            resultVector.put(term,weight);
        }
        return resultVector;
    }

    private static Map<Term,Double> constructNewQuery(Map<Term,Double> oldQueryVector,
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

            for (Map.Entry<Term,Double> entry : unrelevantDocumentVector.entrySet()) {
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

        Set<Term> resultTerms = queryVector.keySet();
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
