package stbi;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import formstubs.IndexingDocumentStub;
import formstubs.StopwordsStub;
import play.Play;
import play.libs.Json;
import stbi.common.IndexedDocument;
import stbi.common.Loader;
import stbi.common.Option;
import stbi.common.index.Index;
import stbi.common.index.ModifiedInvertedIndex;
import stbi.common.term.Term;
import stbi.common.util.Calculator;
import stbi.common.util.Pair;
import stbi.indexer.ModifiedInvertedIndexer;
import stbi.indexer.RawDocument;
import stbi.searcher.Searcher;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Application Logic.
 * Handle all application features.
 */
public class ApplicationLogic {

    public static final String INDEXING_SETTING_PATH = "res/indexing.json";
    private static final String STOPWORD_PATH = "res/stopwords.json";

    private final Loader loader = new Loader();
    private final Calculator calculator = new Calculator();
    private final ModifiedInvertedIndexer modifiedInvertedIndexer = new ModifiedInvertedIndexer(calculator);
    private final Searcher searcher = new Searcher(calculator);
    private final File indexFile;
    private final File indexSettingFile;
    private final File stopwordFile;

    private Index index;
    private Set<Term> stopwords;
    private Option searchOption;

    private ApplicationLogic() {
        indexFile = Play.application().getFile("res/index.idx");
        indexSettingFile = Play.application().getFile(INDEXING_SETTING_PATH);
        stopwordFile = Play.application().getFile(STOPWORD_PATH);
    }

    public boolean indexFileExists() {
        return indexFile.exists() && !indexFile.isDirectory();
    }

    public IndexingDocumentStub getIndexingDocumentObjectFromJson() {
        IndexingDocumentStub indexingDocumentStub = null;
        if(indexSettingFile.exists() && !indexSettingFile.isDirectory()) {
            ObjectMapper mapper = new ObjectMapper();

            BufferedReader fileReader = null;
            try {
                fileReader = new BufferedReader(
                        new FileReader(INDEXING_SETTING_PATH));
                JsonNode json = mapper.readTree(fileReader);
                indexingDocumentStub = Json.fromJson(json, IndexingDocumentStub.class);

                StopwordsStub stopwordsStub = getStopwordsDocumentObjectFromJson();
                if (stopwordsStub != null) {
                    indexingDocumentStub.setStopwordLocation(stopwordsStub.getStopwordLocation());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return indexingDocumentStub;
    }

    public StopwordsStub getStopwordsDocumentObjectFromJson() {
        StopwordsStub stopwordsStub = null;
        if(stopwordFile.exists() && !stopwordFile.isDirectory()) {
            ObjectMapper mapper = new ObjectMapper();

            BufferedReader fileReader = null;
            try {
                fileReader = new BufferedReader(
                        new FileReader(STOPWORD_PATH));
                JsonNode json = mapper.readTree(fileReader);
                stopwordsStub = Json.fromJson(json, StopwordsStub.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stopwordsStub;
    }
    
    public void setSearchOptions(Option _searchOption) {
        searchOption = _searchOption;
    }

    public void indexDocuments(File documentsFile, File stopwordsFile, Calculator.TFType tfType, boolean useIDF,
                               boolean useNormalization, boolean useStemmer) throws IOException {
        List<RawDocument> documents = loader.loadAllDocuments(documentsFile);

        // load stopwords
        String[] stopwordsArray = loader.loadStopwords(stopwordsFile);
        Set<Term> stopwordsSet = new HashSet<>();
        for (String stopwordString : stopwordsArray) {
            Term stopword = new Term(stopwordString);
            stopwordsSet.add(stopword);
        }
        stopwords = stopwordsSet;

        index = modifiedInvertedIndexer.createIndex(documents, stopwordsSet, tfType, useIDF,
                useNormalization, useStemmer);
        loader.saveIndex(indexFile, (ModifiedInvertedIndex) index);
    }

    public List<Pair<Double, Integer>> searchQuery(String query) throws IndexedFileException {
        if (stopwords == null && index == null) throw new IndexedFileException(); // TODO ini maksudnya or bukan?
        return searcher.search(index, query, stopwords, searchOption.getTfType(), searchOption.isUseIDF(),
                searchOption.isUseNormalization(), searchOption.isUseStemmer());
    }

    public IndexedDocument getIndexedDocument(int docID) {
        return index.getIndexedDocument(docID);
    }

    private static final ApplicationLogic oneInstance = new ApplicationLogic();

    public static ApplicationLogic getInstance() {
        return oneInstance;
    }

    public void saveStopwordsLocation(StopwordsStub stopwordsStub) throws IOException {
        new ObjectMapper().writeValue(new File(STOPWORD_PATH), stopwordsStub);
    }

    public void saveIndexingDocumentSetting(IndexingDocumentStub indexingDocumentStub) throws IOException {
        new ObjectMapper().writeValue(new File(INDEXING_SETTING_PATH), indexingDocumentStub);
    }
}
