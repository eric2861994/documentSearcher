package stbi.indexer;

import stbi.common.TermFrequency;
import stbi.common.term.StringTermStream;
import sun.security.ec.ECDSASignature;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modified Inverted Indexer.
 * <p/>
 * TODO move stopwords loading to other module as it might be used in searcher too.
 * <p/>
 * This class will create Modified Inverted Index from documents.
 */
public class ModifiedInvertedIndexer {

    private final File documentFiles;
    private String[] stopwords;

    ModifiedInvertedIndexer(File _documentsFile, File stopwordsFile) {
        documentFiles = _documentsFile;
    }

    void createIndex() {
        // load all documents

        RawDocument[] documents = new RawDocument[0];

        // find term frequency for each document
        TermFrequency[] termFrequencies = new TermFrequency[documents.length];

        int idx = 0;
        for (RawDocument oneDocument : documents) {
            String body = oneDocument.getBody();
            StringTermStream stringTermStream = new StringTermStream(body);
            TermFrequency termFrequency = new TermFrequency(stringTermStream);

            termFrequencies[idx] = termFrequency;

            idx++;
        }

        /*
        1. discover all documents

        3. weight it using specific algorithm
        4. create document model for each document
        5. create index
         */
    }

    /**
     * Load all documents from documentsFile.
     * <p/>
     * TODO please implement this Kevin Yudi Utama
     * For the format please refer to README in dataset.
     */
    List<RawDocument> loadAllDocuments() throws IOException {
        FileReader fileReader = new FileReader(documentFiles);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        /*
        TODO implement
        for each document, make a RawDocument instance
        parse every entry of a document into RawDocument
        return array of RawDocument
         */

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
                if (line.charAt(0) == RawDocument.SECTION_TOKEN) {
                    //BEGINNING OF NEW SECTION

                    currentSection = line.charAt(0);
                    if (line.charAt(1) == RawDocument.SECTION_ID && line.length()>=3) {
                        //BEGINNING OF NEW DOCUMENTS

                        if (numOfDocument!=0) {
                            rawDocuments.add(new RawDocument(id,
                                    title.toString(),
                                    author.toString(),
                                    body.toString()));
                            id = -1;
                            title.setLength(0);
                            author.setLength(0);
                            body.setLength(0);
                        }
                        
                        id = Integer.parseInt(line.substring(3,3));
                    }
                    numOfDocument++;

                } else {
                    switch (currentSection) {
                        case RawDocument.SECTION_TITLE:
                            title.append(line);
                            break;
                        case RawDocument.SECTION_AUTHOR:
                            author.append(line);
                            break;
                        case RawDocument.SECTION_CONTENT:
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

    private RawDocument getRawDocumentFromDocumentString(String documentString) {
        String splitString[] = documentString.split("\n");


    }

    void setStopwords(File stopwordsFile) throws IOException {
        stopwords = loadStopwords(stopwordsFile);
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
