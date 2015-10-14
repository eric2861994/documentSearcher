package stbi;

import play.Play;
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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Application Logic.
 * Handle all application features.
 */
public class ApplicationLogic {
    private final Loader loader = new Loader();
    private final Calculator calculator = new Calculator();
    private final ModifiedInvertedIndexer modifiedInvertedIndexer = new ModifiedInvertedIndexer(calculator);
    private final Searcher searcher = new Searcher(calculator);
    private final File indexFile;

    private Index index;
    private Set<Term> stopwords;
    private Option searchOption;

    private ApplicationLogic() {
        indexFile = Play.application().getFile("res/index.idx");
    }

    public void setSearchOptions(Option _searchOption) {
        searchOption = _searchOption;
    }

    public void indexDocuments(File documentsFile, File stopwordsFile, Calculator.TFType tfType, boolean useIDF,
                               boolean useNormalization, boolean useStemmer) throws IOException {
        List<RawDocument> documents = loader.loadAllDocuments(documentsFile);

        loader.loadStopwords(stopwordsFile);
//        modifiedInvertedIndexer.createIndex();

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

    public List<Pair<Double, Integer>> searchQuery(String query) {
        return searcher.search(index, query, stopwords, searchOption.getTfType(), searchOption.isUseIDF(),
                searchOption.isUseNormalization(), searchOption.isUseStemmer());
    }

    public void performExperiment() {

    }

    public IndexedDocument getIndexedDocument(int docID) {
        return index.getIndexedDocument(docID);
    }

    private static final ApplicationLogic oneInstance = new ApplicationLogic();

    public static ApplicationLogic getInstance() {
        return oneInstance;
    }
}
