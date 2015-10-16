package stbi.indexer;

import stbi.common.IndexedDocument;
import stbi.common.TermFrequency;
import stbi.common.index.ModifiedInvertedIndex;
import stbi.common.term.*;
import stbi.common.term.stemmer.PorterStemmer;
import stbi.common.term.stemmer.Stemmer;
import stbi.common.util.Calculator;

import java.io.IOException;
import java.util.*;

/**
 * Modified Inverted Indexer.
 * <p/>
 * TODO move stopwords loading to other module as it might be used in searcher too.
 * <p/>
 * This class will create Modified Inverted Index from documents.
 */
public class ModifiedInvertedIndexer {

    private final Calculator calculator;

    public ModifiedInvertedIndexer(Calculator _calculator) {
        calculator = _calculator;
    }

    public ModifiedInvertedIndex createIndex(List<RawDocument> rawDocumentsList, Set<Term> stopwords, Calculator.TFType tfType, boolean useIDF, boolean useNormalization, boolean useStemmer) throws IOException {
        // load all documents
        RawDocument[] documents = rawDocumentsList.toArray(new RawDocument[rawDocumentsList.size()]);

        // find term frequency for each document
        TermFrequency[] termFrequencies = new TermFrequency[documents.length];
        for (int docIdx = 0; docIdx < documents.length; docIdx++) {
            RawDocument oneDocument = documents[docIdx];

            // only title and body is used in indexing
            String searchString = oneDocument.getAuthor() + " " + oneDocument.getBody();

            StringTermStream stringTermStream = new StringTermStream(searchString, "\\W");
            StopwordTermStream stopwordTermStream = new StopwordTermStream(stringTermStream, stopwords);
            TermStream termStream = stopwordTermStream;
            if (useStemmer) {
                Stemmer porterStemmer = new PorterStemmer();
                termStream = new StemmingTermStream(stopwordTermStream, porterStemmer);
            }

            termFrequencies[docIdx] = new TermFrequency(termStream);
        }

        // calculate weight using TF
        List<Map<Term, Double>> documentWeightList = new ArrayList<>();
        for (TermFrequency oneTermFrequency : termFrequencies) {
            Map<Term, Double> documentWeight = calculator.getTFValue(tfType, oneTermFrequency);
            documentWeightList.add(documentWeight);
        }

        // consider idf
        if (useIDF) {
            // count # of documents for each term
            Map<Term, Integer> termDocumentCount = new HashMap<>();
            for (TermFrequency oneTermFrequency : termFrequencies) {
                for (Term term : oneTermFrequency.getTerms()) {
                    int currentCount = 1;
                    if (termDocumentCount.containsKey(term)) {
                        currentCount = termDocumentCount.get(term) + 1;
                    }

                    termDocumentCount.put(term, currentCount);
                }
            }

            // redefine the weight of each term
            for (Map<Term, Double> documentWeight : documentWeightList) {
                for (Term term : documentWeight.keySet()) {
                    double previousWeight = documentWeight.get(term);
                    documentWeight.put(term, previousWeight * termIDF(termDocumentCount.get(term), documentWeightList.size()));
                }
            }
        }

        // consider normalization
        if (useNormalization) {
            // calculate length of each document
            double[] documentLength = new double[documents.length];
            for (int docIdx = 0; docIdx < documents.length; docIdx++) {
                double oneDocumentLength = 0;
                Map<Term, Double> documentWeight = documentWeightList.get(docIdx);
                for (Term term : documentWeight.keySet()) {
                    oneDocumentLength += documentWeight.get(term) * documentWeight.get(term);
                }
                documentLength[docIdx] = Math.sqrt(oneDocumentLength);
            }

            // divide every term weight of a document by the document's length
            for (int docIdx = 0; docIdx < documents.length; docIdx++) {
                Map<Term, Double> documentWeight = documentWeightList.get(docIdx);
                for (Term term : documentWeight.keySet()) {
                    double previousWeight = documentWeight.get(term);
                    documentWeight.put(term, previousWeight / documentLength[docIdx]);
                }
            }
        }

        // create index
        IndexedDocument[] indexedDocuments = new IndexedDocument[documents.length];
        for (int docIdx = 0; docIdx < documents.length; docIdx++) {
            indexedDocuments[docIdx] = new IndexedDocument(documents[docIdx]);
        }
        Map<Term, Map<Integer, Double>> termDocumentWeight = new HashMap<>();
        for (int docIdx = 0; docIdx < documents.length; docIdx++) {
            Map<Term, Double> documentWeight = documentWeightList.get(docIdx);
            for (Term term : documentWeight.keySet()) {
                if (termDocumentWeight.containsKey(term)) {
                    Map<Integer, Double> previousMap = termDocumentWeight.get(term);
                    previousMap.put(docIdx, documentWeight.get(term));

                } else {
                    Map<Integer, Double> newMap = new HashMap<>();
                    newMap.put(docIdx, documentWeight.get(term));
                    termDocumentWeight.put(term, newMap);
                }
            }
        }
        return new ModifiedInvertedIndex(termDocumentWeight, indexedDocuments);
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
