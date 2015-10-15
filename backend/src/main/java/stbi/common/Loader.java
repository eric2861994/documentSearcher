package stbi.common;

import stbi.indexer.RawDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Files Loader.
 */
public class Loader {
    /**
     * Get stopwords from a file.
     * <p/>
     * File containing Stopwords is separated by a newline and ends with a newline.
     *
     * @param stopwordsFile file containing the stopwords
     * @return stopwords
     * @throws IOException this will rarely happen as File is checked before passed to this class
     */
    public String[] loadStopwords(File stopwordsFile) throws IOException {
        FileReader fileReader = new FileReader(stopwordsFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        List<String> stopwords = new ArrayList<>();
        try {
            // just return all the stopwords extracted from each line of the file.
            String stopword;
            while ((stopword = bufferedReader.readLine()) != null) {
                stopwords.add(stopword);
            }

        } finally {
            bufferedReader.close();
        }

        return stopwords.toArray(new String[0]);
    }

    /**
     * Load all documents from documentsFile.
     * <p/>
     * For the format please refer to README in dataset.
     */
    public List<RawDocument> loadAllDocuments(File documentsFile) throws IOException {
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
                            if (!author.toString().equals((""))) author.append("\n");
                            author.append(line);
                            break;
                        case SECTION_CONTENT:
                            if (!body.toString().equals((""))) body.append("\n");
                            body.append(line);
                            break;
                        default:
                            break;
                    }
                }
                line = bufferedReader.readLine();
            }

            if (id!=-1) {
                rawDocuments.add(new RawDocument(id,
                        title.toString(),
                        author.toString(),
                        body.toString()));
            }

        } finally {
            bufferedReader.close();
        }

        return rawDocuments;
    }

    //Raw Document FORMAT;
    public static final char SECTION_TOKEN = '.';
    public static final char SECTION_ID = 'I';
    public static final char SECTION_TITLE = 'T';
    public static final char SECTION_AUTHOR = 'A';
    public static final char SECTION_CONTENT = 'W';
    public static final char SECTION_INDEX = 'X';
}
