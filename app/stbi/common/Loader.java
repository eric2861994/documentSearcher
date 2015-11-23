package stbi.common;

import stbi.common.index.ModifiedInvertedIndex;
import stbi.common.term.Term;
import stbi.indexer.RawDocument;

import java.io.*;
import java.util.*;

/**
 * Files Loader.
 */
public class Loader {
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

                        if (line.length() > 3) id = Integer.parseInt(line.substring(3));
                    }
                    numOfDocument++;

                } else {
                    switch (currentSection) {
                        case SECTION_TITLE:
                            if (!title.toString().equals((""))) title.append(" ");
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

            if (id != -1) {
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

    /**
     * Get stopwords from a file.
     * File containing Stopwords is separated by a newline and ends with a newline.
     *
     * @param stopwordsFile file containing the stopwords
     * @return stopwords
     * @throws IOException when there is problem in reading the file
     */
    public Set<Term> loadStopwords(File stopwordsFile) throws IOException {
        FileReader fileReader = new FileReader(stopwordsFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        List<Term> stopwords = new ArrayList<>();
        try {
            // just return all the stopwords extracted from each line of the file.
            String stopword;
            while ((stopword = bufferedReader.readLine()) != null) {
                Term term = new Term(stopword);
                stopwords.add(term);
            }

        } finally {
            bufferedReader.close();
        }

        Set<Term> result = new HashSet<>();
        result.addAll(stopwords);

        return result;
    }

    /**
     * Save index to file.
     *
     * @param indexFile             index file
     * @param modifiedInvertedIndex index
     * @throws IOException
     */
    public void saveIndex(File indexFile, ModifiedInvertedIndex modifiedInvertedIndex) throws IOException {
        OutputStream outputStream = new FileOutputStream(indexFile);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.writeObject(modifiedInvertedIndex);

        } finally {
            if (objectOutputStream != null) {
                objectOutputStream.close();

            } else {
                outputStream.close();
            }
        }
    }

    /**
     * Save index to readable csv format
     *
     * @param indexFile indexFile
     * @param modifiedInvertedIndex index
     * @throws IOException
     */
    public void saveReadableIndex(File indexFile, ModifiedInvertedIndex modifiedInvertedIndex) throws IOException{
        PrintWriter printWriter = new PrintWriter(indexFile);

        try {
            Map<Term, Map<Integer, Double>> termDocumentWeight = modifiedInvertedIndex.getTermDocumentWeight();
            printWriter.println("\"Term\",\"Document\",\"Weight\"");
            for (Map.Entry<Term, Map<Integer, Double>> documentWeightEntry : termDocumentWeight.entrySet()) {
                boolean firstRow = true;
                for (Map.Entry<Integer, Double> weightEntry : documentWeightEntry.getValue().entrySet()) {
                    if (firstRow) {
                        printWriter.printf("\"%s\"", documentWeightEntry.getKey().toString());
                        firstRow = false;
                    }
                    int documentId = modifiedInvertedIndex.getIndexedDocument(weightEntry.getKey()).getId();
                    printWriter.printf(",%d,%f\n", documentId, weightEntry.getValue());
                }
            }
        } finally {
            printWriter.close();
        }

    }

    /**
     * Load index from file
     *
     * @param indexFile index file
     * @return modified inverted index
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ModifiedInvertedIndex loadIndex(File indexFile) throws IOException, ClassNotFoundException {
        InputStream inputStream = new FileInputStream(indexFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        ObjectInputStream objectInputStream = null;
        ModifiedInvertedIndex modifiedInvertedIndex = null;
        try {
            objectInputStream = new ObjectInputStream(bufferedInputStream);
            modifiedInvertedIndex = (ModifiedInvertedIndex) objectInputStream.readObject();

        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            } else {
                inputStream.close();
            }
        }

        return modifiedInvertedIndex;
    }

    //Raw Document FORMAT;
    public static final char SECTION_TOKEN = '.';
    public static final char SECTION_ID = 'I';
    public static final char SECTION_TITLE = 'T';
    public static final char SECTION_AUTHOR = 'A';
    public static final char SECTION_CONTENT = 'W';
    public static final char SECTION_INDEX = 'X';
}
