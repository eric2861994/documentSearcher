package stbi.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Modified Inverted Indexer.
 *
 * TODO move stopwords loading to other module as it might be used in searcher too.
 *
 * This class will create Modified Inverted Index from documents.
 */
public class ModifiedInvertedIndexer {

    private final File documentFile;
    private String[] stopwords;

    ModifiedInvertedIndexer(File _documentsFile, File stopwordsFile) {
        documentFile = _documentsFile;
    }

    void createIndex() {
        // load all documents

        /*
        1. discover all documents

        2. find term frequency for each document
        3. weight it using specific algorithm
        4. create document model for each document
        5. create index
         */
    }

    /**
     * Load all documents from documentsFile.
     *
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

    void setStopwords(File stopwordsFile) throws FileNotFoundException {
        stopwords = loadStopwords(stopwordsFile);
    }

    /**
     * Get stopwords from a file.
     *
     * File containing Stopwords is separated by a newline and ends with a newline.
     * TODO implement by Winson
     *
     * @param stopwordsFile    file containing the stopwords
     * @return stopwords
     * @throws FileNotFoundException this will rarely happen as File is checked before passed to this class
     */
    private String[] loadStopwords(File stopwordsFile) throws FileNotFoundException {
        FileReader fileReader = new FileReader(stopwordsFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        List<String> soptwords = new ArrayList<>();

        // TODO implement
        // just return all the stopwords extracted from each line of the file.

        return null;
    }
}
