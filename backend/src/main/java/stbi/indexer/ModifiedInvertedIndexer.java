package stbi.indexer;

import stbi.common.IndexTermWeighter;
import stbi.common.IndexedDocument;
import stbi.common.TermFrequency;
import stbi.common.TermWeighter;
import stbi.common.index.ModifiedInvertedIndex;
import stbi.common.term.StringTermStream;
import stbi.common.term.Term;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    private final File documentsFile;
    private final String[] stopwords;
    private final IndexTermWeighter indexTermWeighter = new IndexTermWeighter();

    ModifiedInvertedIndexer(File _documentsFile, String[] _stopwords) {
        documentsFile = _documentsFile;
        stopwords = _stopwords;
    }

    ModifiedInvertedIndex createIndex(TermWeighter termWeighter, boolean useIDF, boolean useNormalization) throws IOException {
        // load all documents
        List<RawDocument> rawDocumentsList = loadAllDocuments();
        RawDocument[] documents = (RawDocument[]) rawDocumentsList.toArray(new RawDocument[rawDocumentsList.size()]);

        System.out.println("Loaded all documents");

        // find term frequency for each document
        TermFrequency[] termFrequencies = new TermFrequency[documents.length];
        for (int docIdx = 0; docIdx < documents.length; docIdx++) {
            RawDocument oneDocument = documents[docIdx];

            // TODO here we assume only the document's body is used in indexing
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
     * For the format please refer to README in dataset.
     */
    public List<RawDocument> loadAllDocuments() throws IOException {
        FileReader fileReader = new FileReader(documentsFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        ArrayList<RawDocument> rawDocuments = new ArrayList<>();

        try {
            String line = bufferedReader.readLine();

            int id = -1;
            StringBuilder title = new StringBuilder("");
            StringBuilder author = new StringBuilder("");
            StringBuilder body = new StringBuilder("");

            int numOfDocument = 0;

            char currentSection = '-';

            while (line != null) {
                if (line.length() > 0 && line.charAt(0) == SECTION_TOKEN) {
                    //BEGINNING OF NEW SECTION

                    currentSection = line.charAt(1);
                    if (currentSection == SECTION_ID) {
                        //BEGINNING OF NEW DOCUMENTS

                        if (numOfDocument != 0) {
                            rawDocuments.add(new RawDocument(id,
                                    title.toString(),
                                    author.toString(),
                                    body.toString()));
                            id = -1;
                            title.setLength(0);
                            author.setLength(0);
                            body.setLength(0);
                        }

                        if (line.length() > 3) id = Integer.parseInt(line.substring(3)); // TODO fix this kayu
                    }
                    numOfDocument++;

                } else {
                    switch (currentSection) {
                        case SECTION_TITLE:
                            if (!title.toString().equals((""))) title.append("\n");
                            title.append(line);
                            break;
                        case SECTION_AUTHOR:
                            if (!author.toString().equals((""))) title.append("\n");
                            author.append(line);
                            break;
                        case SECTION_CONTENT:
                            if (!body.toString().equals((""))) title.append("\n");
                            body.append(line);
                            break;
                        default:
                            break;
                    }
                }
                line = bufferedReader.readLine();
            }

            rawDocuments.add(new RawDocument(id,
                    title.toString(),
                    author.toString(),
                    body.toString()));

        } finally {
            bufferedReader.close();
        }

        return rawDocuments;
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

    //Raw Document FORMAT;
    public static final char SECTION_TOKEN = '.';
    public static final char SECTION_ID = 'I';
    public static final char SECTION_TITLE = 'T';
    public static final char SECTION_AUTHOR = 'A';
    public static final char SECTION_CONTENT = 'W';
    public static final char SECTION_INDEX = 'X';
}
