package stbi.indexer;

import stbi.common.IndexTermWeighter;
import stbi.common.IndexedDocument;
import stbi.common.TermFrequency;
import stbi.common.TermWeighter;
import stbi.common.index.ModifiedInvertedIndex;
import stbi.common.term.StringTermStream;
import stbi.common.term.Term;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modified Inverted Indexer.
 * <p/>
 * TODO move stopwords loading to other module as it might be used in searcher too.
 * <p/>
 * This class will create Modified Inverted Index from documents.
 */
public class ModifiedInvertedIndexer {

    private final File documentFile;
    private final String[] stopwords;
    private final IndexTermWeighter indexTermWeighter = new IndexTermWeighter();

    ModifiedInvertedIndexer(File _documentsFile, String[] _stopwords) {
        documentFile = _documentsFile;
        stopwords = _stopwords;
    }

    ModifiedInvertedIndex createIndex(TermWeighter termWeighter, boolean useIDF, boolean useNormalization) {
        // load all documents
        RawDocument[] documents = new RawDocument[0];

        // find term frequency for each document
        TermFrequency[] termFrequencies = new TermFrequency[documents.length];
        for (int docIdx = 0; docIdx < documents.length; docIdx++) {
            RawDocument oneDocument = documents[docIdx];

            // TODO here we assume only the document's body is used indexing
            String body = oneDocument.getBody();
            StringTermStream stringTermStream = new StringTermStream(body);
            TermFrequency termFrequency = new TermFrequency(stringTermStream);

            termFrequencies[docIdx] = termFrequency;
        }

        // create a map from a term to document's index
        Map<Term, List<Integer>> termDocument = new HashMap<>();
        for (int docIdx = 0; docIdx < documents.length; docIdx++) {
            TermFrequency documentTermFrequency = termFrequencies[docIdx];

            for (Term term : documentTermFrequency.getTerms()) {
                // make term map include current document
                if (termDocument.containsKey(term)) {
                    List<Integer> documentList = termDocument.get(term);
                    documentList.add(docIdx);

                } else {
                    List<Integer> documentList = new ArrayList<>();
                    documentList.add(docIdx);
                    termDocument.put(term, documentList);
                }
            }
        }

        // weight documents using specific algorithm
        List<Map<Term, Double>> documentVector = indexTermWeighter.weightAllDocuments(
                termWeighter, useIDF, useNormalization, termFrequencies, termDocument);

        // create document model for each document
        IndexedDocument[] indexedDocuments = new IndexedDocument[documents.length];
        for (int docIdx = 0; docIdx < documents.length; docIdx++) {
            RawDocument oneDocument = documents[docIdx];
            IndexedDocument oneIndexedDocument = new IndexedDocument(oneDocument, documentVector.get(docIdx));

            indexedDocuments[docIdx] = oneIndexedDocument;
        }

        // create index
        return new ModifiedInvertedIndex(termDocument, indexedDocuments);
    }

    /**
     * Load all documents from documentsFile.
     * <p/>
     * TODO please implement this Kevin Yudi Utama
     * For the format please refer to README in dataset.
     */
    RawDocument[] loadAllDocuments() throws FileNotFoundException {
        FileReader fileReader = new FileReader(documentFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        /*
        TODO implement
        for each document, make a RawDocument instance
        parse every entry of a document into RawDocument
        return array of RawDocument
         */
        return null;
    }

    void setStopwords(File stopwordsFile) throws IOException {
        loadStopwords(stopwordsFile);
    }

    /**
     * Get stopwords from a file.
     * <p/>
     * File containing Stopwords is separated by a newline and ends with a newline.
     *
     * @param stopwordsFile file containing the stopwords
     * @return stopwords
     * @throws IOException this will rarely happen as File is checked before passed to this class
     */
    private String[] loadStopwords(File stopwordsFile) throws IOException {
        FileReader fileReader = new FileReader(stopwordsFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        List<String> stopwords = new ArrayList<>();

        // just return all the stopwords extracted from each line of the file.
        String stopword;
        while ((stopword = bufferedReader.readLine()) != null) {
            stopwords.add(stopword);
        }

        return (String[]) stopwords.toArray();
    }
}
