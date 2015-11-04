package stbi.searcher;

import stbi.common.TermFrequency;
import stbi.common.index.Index;
import stbi.common.term.*;
import stbi.common.term.stemmer.PorterStemmer;
import stbi.common.term.stemmer.Stemmer;
import stbi.common.util.Calculator;
import stbi.common.util.Pair;

import java.util.*;

/**
 * Handles searching query
 */
public class Searcher {
    private final Calculator calculator;

    public Searcher(Calculator _calculator) {
        calculator = _calculator;
    }

    public List<Pair<Double, Integer>> search(Index index, String query, Set<Term> stopwords, Calculator.TFType tfType,
                                              boolean isUsingIdf, boolean isNormalized, boolean useStemmer) {
        // Make Term Stream from the query.
        StringTermStream stringTermStream = new StringTermStream(query, "\\W+");
        StopwordTermStream stopwordTermStream = new StopwordTermStream(stringTermStream, stopwords);
        TermStream termStream = stopwordTermStream;
        if (useStemmer) {
            Stemmer stemmer = new PorterStemmer();
            termStream = new StemmingTermStream(stopwordTermStream, stemmer);
        }

        // Calculate Term Frequency from the Term Stream.
        TermFrequency termFrequency = new TermFrequency(termStream);

        Map<Term, Double> termsWeight = calculator.getTFValue(tfType, termFrequency);
        if (isUsingIdf) addIdfToWeight(termsWeight, index);
        if (isNormalized) normalizeTermsWeight(termsWeight);

        // Find the indexedDocuments that contain the query.
        Integer[] indexDocIDs = index.getDocumentIDs(termFrequency.getTerms());

        // for each document we compute the similarity of it with query
        List<Pair<Double, Integer>> similarityDocIDList = new ArrayList<>();
        for (Integer documentID : indexDocIDs) {
            double similarity = index.getSimilarity(termsWeight, documentID);

            similarityDocIDList.add(new Pair<>(similarity, documentID));
        }

        // sort indexedDocuments ascending by similarity
        Collections.sort(similarityDocIDList);

        // select indexedDocuments with similarity > 0
        List<Pair<Double, Integer>> selectedSimilairtyDocIDs = new ArrayList<>();
        for (Pair<Double, Integer> similarityDocID : similarityDocIDList) {
            if (similarityDocID.first > EPSILON) {
                selectedSimilairtyDocIDs.add(similarityDocID);
            }
        }

        // reverse the selected documents so the document with highest similarity is on front
        Collections.reverse(selectedSimilairtyDocIDs);
        return selectedSimilairtyDocIDs;
    }

    private void normalizeTermsWeight(Map<Term, Double> termsWeight) {
        double weightLength = 0.0;
        for (Map.Entry<Term, Double> termDoubleEntry : termsWeight.entrySet()) {
            weightLength += (termDoubleEntry.getValue() * termDoubleEntry.getValue());
        }
        weightLength = Math.sqrt(weightLength);
        for (Map.Entry<Term, Double> termDoubleEntry : termsWeight.entrySet()) {
            termDoubleEntry.setValue(termDoubleEntry.getValue() / weightLength);
        }
    }

    private void addIdfToWeight(Map<Term, Double> termsWeight, Index index) {
        for (Map.Entry<Term, Double> termDoubleEntry : termsWeight.entrySet()) {
            if (index.getDocumentCount(termDoubleEntry.getKey()) > 0) {
                double idf = Math.log10((double) index.getAllDocumentCount() / index.getDocumentCount(termDoubleEntry.getKey()));
                termDoubleEntry.setValue(termDoubleEntry.getValue() * idf);

            } else {
                termDoubleEntry.setValue(0.0);
            }
        }
    }

    private static final double EPSILON = 1E-6;
}
