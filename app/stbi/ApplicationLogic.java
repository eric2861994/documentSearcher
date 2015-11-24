package stbi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import formstubs.ExperimentalRetrievalStub;
import formstubs.IndexingDocumentStub;
import formstubs.InteractiveRetrievalStub;
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
import stbi.common.util.RelevanceFeedbackOption;
import stbi.indexer.ModifiedInvertedIndexer;
import stbi.indexer.RawDocument;
import stbi.relevance.RelevanceJudge;
import stbi.searcher.Searcher;

import java.io.*;
import java.util.*;

/**
 * Application Logic.
 * Handle all application features.
 */
public class ApplicationLogic {

    private static final String INDEXING_SETTING_PATH = "res/indexing.json";
    private static final String STOPWORD_SETTING_PATH = "res/stopwords.json";
    private static final String INTERACTIVE_RETRIEVAL_PATH = "res/interactive.json";
    private static final String EXPERIMENTAL_RETRIEVAL_PATH = "res/experimental.json";
    private static final Set<Term> emptyStopword = new HashSet<>();


    private static final String INDEX_FILE_PATH = "res/index.idx";
    private final Loader loader = new Loader();
    private final Calculator calculator = new Calculator();
    private final ModifiedInvertedIndexer modifiedInvertedIndexer = new ModifiedInvertedIndexer(calculator);
    private final Searcher searcher = new Searcher(calculator);
    private final File indexFile;
    private final File readableIndexFile;
    private final File indexSettingFile;
    private final File stopwordSettingFile;
    private final File interactiveSearchSettings;
    private final File experimentalRetrievalQueryFile;

    private Index index;
    private Set<Term> stopwords;
    private List<ExperimentResult> experimentResult;
    private Map<Term, Double> firstSearchQuery;

    private ApplicationLogic() {
        indexFile = Play.application().getFile(INDEX_FILE_PATH);
        readableIndexFile = Play.application().getFile(INDEX_FILE_PATH + "csv");
        indexSettingFile = Play.application().getFile(INDEXING_SETTING_PATH);
        stopwordSettingFile = Play.application().getFile(STOPWORD_SETTING_PATH);
        interactiveSearchSettings = Play.application().getFile(INTERACTIVE_RETRIEVAL_PATH);
        experimentalRetrievalQueryFile = Play.application().getFile(EXPERIMENTAL_RETRIEVAL_PATH);

        // try to load stopwords
        StopwordsStub stopwordsSetting = loadStopwordSetting();
        if (stopwordsSetting != null) {
            File stopwordFile = new File(stopwordsSetting.getStopwordLocation());
            try {
                stopwords = loader.loadStopwords(stopwordFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (stopwords == null) {
            stopwords = emptyStopword;
        }

        // try to load index
        try {
            index = loader.loadIndex(indexFile);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ExperimentalRetrievalStub getExperimentSettings() {
        ExperimentalRetrievalStub experimentalRetrievalStub = new ExperimentalRetrievalStub();
        Option option = getExperimentOption();
        experimentalRetrievalStub.setTf(option.getTfType());
        experimentalRetrievalStub.setUseIdf(option.isUseIDF());
        experimentalRetrievalStub.setUseNormalization(option.isUseNormalization());
        experimentalRetrievalStub.setUseStemmer(option.isUseStemmer());
        experimentalRetrievalStub.setRelevanceFeedbackStatus(option.getRelevanceFeedbackStatus());
        experimentalRetrievalStub.setUseSameDocumentCollection(option.isUseSameDocumentCollection());
        experimentalRetrievalStub.setS(option.getS());
        experimentalRetrievalStub.setN(option.getN());
        experimentalRetrievalStub.setUseQueryExpansion(option.isUseQueryExpansion());
        return experimentalRetrievalStub;
    }


    public void indexDocuments(File documentsFile, Calculator.TFType tfType, boolean useIDF,
                               boolean useNormalization, boolean useStemmer) throws IOException {
        // stopwords is guaranteed to be not null
        List<RawDocument> documents = loader.loadAllDocuments(documentsFile);
        index = modifiedInvertedIndexer.createIndex(documents, stopwords, tfType, useIDF, useNormalization, useStemmer);

        loader.saveIndex(indexFile, (ModifiedInvertedIndex) index);
        loader.saveReadableIndex(readableIndexFile, (ModifiedInvertedIndex) index);
    }

    public boolean indexLoaded() {
        return index != null;
    }

    public IndexingDocumentStub loadIndexingSettings() {
        IndexingDocumentStub indexingDocumentStub = new IndexingDocumentStub();
        if (indexSettingFile.exists() && !indexSettingFile.isDirectory()) {
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(indexSettingFile));
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(fileReader);
                indexingDocumentStub = Json.fromJson(jsonNode, IndexingDocumentStub.class);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        StopwordsStub stopwordsStub = loadStopwordSetting();
        if (stopwordsStub != null) {
            indexingDocumentStub.setStopwordLocation(stopwordsStub.getStopwordLocation());
        }

        return indexingDocumentStub;
    }

    public final StopwordsStub loadStopwordSetting() {
        StopwordsStub stopwordsStub = null;
        if (stopwordSettingFile.exists() && !stopwordSettingFile.isDirectory()) {
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(stopwordSettingFile));
                ObjectMapper mapper = new ObjectMapper();
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

    public InteractiveRetrievalStub loadInteractiveSearchSettings() {
        InteractiveRetrievalStub interactiveRetrievalStub = new InteractiveRetrievalStub();
        Option option = getSearchOption();
        interactiveRetrievalStub.setTf(option.getTfType());
        interactiveRetrievalStub.setUseIdf(option.isUseIDF());
        interactiveRetrievalStub.setUseNormalization(option.isUseNormalization());
        interactiveRetrievalStub.setUseStemmer(option.isUseStemmer());
        interactiveRetrievalStub.setRelevanceFeedbackStatus(option.getRelevanceFeedbackStatus());
        interactiveRetrievalStub.setRelevanceFeedbackOption(option.getRelevanceFeedbackOption());
        interactiveRetrievalStub.setUseSameDocumentCollection(option.isUseSameDocumentCollection());
        interactiveRetrievalStub.setUseQueryExpansion(option.isUseQueryExpansion());
        interactiveRetrievalStub.setS(option.getS());
        interactiveRetrievalStub.setN(option.getN());
        return interactiveRetrievalStub;
    }

    public void setSearchOption(Option searchOption) throws IOException {
        new ObjectMapper().writeValue(interactiveSearchSettings, searchOption);
    }

    public Option getSearchOption() {
        Option option = new Option();
        if (interactiveSearchSettings.exists() && !interactiveSearchSettings.isDirectory()) {
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(interactiveSearchSettings));
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(fileReader);
                option = Json.fromJson(jsonNode, Option.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return option;
    }

    public void setExperimentOption(Option experimentOption) throws IOException {
        new ObjectMapper().writeValue(experimentalRetrievalQueryFile, experimentOption);
    }

    public Option getExperimentOption() {
        Option option = new Option();
        if (experimentalRetrievalQueryFile.exists() && !experimentalRetrievalQueryFile.isDirectory()) {
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(experimentalRetrievalQueryFile));
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(fileReader);
                option = Json.fromJson(jsonNode, Option.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return option;
    }

    // TODO up from here

    public IndexedDocument getIndexedDocument(int docID) {
        return index.getIndexedDocument(docID);
    }

    /**
     * OUTPUT: query lama, query baru, hasil pencarian baru
     */
    public RelevanceFeedbackDisplayVariables relevanceFeedback(List<Integer> relevantDocumentRealIDs, List<Integer> irrelevantDocumentRealIDs) {
        List<Map<Term, Double>> relevantVectors = new ArrayList<>();
        for (Integer realDocumentID : relevantDocumentRealIDs) {
            relevantVectors.add(index.getDocumentTermVectorUsingRealId(realDocumentID ));
        }

        List<Map<Term, Double>> irrelevantVectors = new ArrayList<>();
        for (Integer realDocumentID : irrelevantDocumentRealIDs) {
            relevantVectors.add(index.getDocumentTermVectorUsingRealId(realDocumentID));
        }

        // harus dibuat disini karena index dapat berubah
        SearcherV2 searcherV2 = new SearcherV2(index, searcher);

        Option searchOption = getSearchOption();
        int reweightMethod = getSearcherV2ReweightMethod(searchOption.getRelevanceFeedbackOption());

        RelevanceFeedbackDisplayVariables result = new RelevanceFeedbackDisplayVariables();

        result.queryLama = firstSearchQuery;
        result.queryBaru = searcherV2.relevanceFeedbackV2(firstSearchQuery, relevantVectors, irrelevantVectors, reweightMethod, searchOption.isUseQueryExpansion());
        result.hasilPencarianBaru = searcher.search(index, result.queryBaru);

        return result;
    }

    public void saveIndexingSettings(IndexingDocumentStub indexingDocumentStub) throws IOException {
        new ObjectMapper().writeValue(indexSettingFile, indexingDocumentStub);
    }

    public void saveStopwordsSetting(StopwordsStub stopwordsStub) throws IOException {
        File stopwordFile = new File(stopwordsStub.getStopwordLocation());
        new ObjectMapper().writeValue(stopwordSettingFile, stopwordsStub);

        stopwords = loader.loadStopwords(stopwordFile);
    }

    public void performExperiment(String queryPath, String relevanceJudgementPath) throws IOException {
        RelevanceJudge relevanceJudge = new RelevanceJudge(
                Play.application().getFile(queryPath),
                Play.application().getFile(relevanceJudgementPath)
        );

        Option experimentOption = getExperimentOption();
        File file = new File(experimentOption.getTfType() + "-" + experimentOption.isUseIDF() + "-" +
                experimentOption.isUseNormalization() + "-" + experimentOption.isUseStemmer() + ".csv");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file);
        writer.write("");

        // get experiment queries
        List<RelevanceJudge.Query> testQueries = relevanceJudge.getQueryList();

        // helper class, forced to declare when we need to use because index might change anytime.
        SearcherV2 searcherV2 = new SearcherV2(index, searcher);

        // experiment result is first stored here before stored to field variable
        List<ExperimentResult> experimentResultList = new ArrayList<>();

        for (RelevanceJudge.Query query : testQueries) {
            Map<Term, Double> initialQuery = searcher.getQueryVector(index, query.queryString, stopwords,
                    experimentOption.getTfType(), experimentOption.isUseIDF(), experimentOption.isUseNormalization(),
                    experimentOption.isUseStemmer());
            List<Pair<Double, Integer>> firstSearchResult = searcher.search(index, initialQuery);

            List<Integer> filterIDList = new ArrayList<>(); // required to call kayu's result evaluation
            List<Pair<Double, Integer>> documentSimilarityList; // the result of second query
            Map<Term, Double> secondQuery = null;
            switch (experimentOption.getRelevanceFeedbackStatus()) {
                case NO_RELEVANCE_FEEDBACK:
                    documentSimilarityList = firstSearchResult; // the second query is the same as first query
                    break;

                case PSEUDO_RELEVANCE_FEEDBACK: {
                    // take the N top documents as relevant document list
                    List<Integer> relevantDocumentList = searcherV2.takeTopDocuments(firstSearchResult, experimentOption.getN());

                    Set<Integer> relevantDocumentSet = new HashSet<>(); // should contain real id
                    for (Integer myID : relevantDocumentList) {
                        IndexedDocument indexedDocument = index.getIndexedDocument(myID);
                        int realID = indexedDocument.getId();
                        relevantDocumentSet.add(realID);
                    }

                    int reweightMethod = getSearcherV2ReweightMethod(experimentOption.getRelevanceFeedbackOption());
                    secondQuery = searcherV2.relevanceFeedback(initialQuery, firstSearchResult, relevantDocumentSet,
                            experimentOption.getN(), reweightMethod, experimentOption.isUseQueryExpansion());
                    documentSimilarityList = searcher.search(index, secondQuery);
                    break;
                }

                case USE_RELEVANCE_FEEDBACK: {
                    Set<Integer> myIDOfDocumentsSeen = new HashSet<>();
                    for (int i = 0; i < experimentOption.getS() && i < firstSearchResult.size(); i++) {
                        myIDOfDocumentsSeen.add(firstSearchResult.get(i).second);
                    }

                    // fill filterIDList with real IDs of document that can be judged
                    for (int i = experimentOption.getS(); i < firstSearchResult.size(); i++) {
                        int myID = firstSearchResult.get(i).second;
                        IndexedDocument indexedDocument = index.getIndexedDocument(myID);
                        int realID = indexedDocument.getId();
                        filterIDList.add(realID);
                    }

                    Map<Integer, Set<Integer>> relevanceJudgement = relevanceJudge.getQueryRelation();
                    Set<Integer> relevantDocumentSet = relevanceJudgement.get(query.id);
                    if (relevantDocumentSet == null) {
                        relevantDocumentSet = new HashSet<>();
                    }

                    int reweightMethod = getSearcherV2ReweightMethod(experimentOption.getRelevanceFeedbackOption());
                    secondQuery = searcherV2.relevanceFeedback(initialQuery, firstSearchResult, relevantDocumentSet,
                            experimentOption.getS(), reweightMethod, experimentOption.isUseQueryExpansion());
                    documentSimilarityList = searcher.search(index, secondQuery);

                    // if we are not allowed to use the same document collection
                    if (!experimentOption.isUseSameDocumentCollection()) {
                        List<Pair<Double, Integer>> temporaryDocumentSimilarityList = new ArrayList<>();

                        // take only unseen result
                        for (Pair<Double, Integer> searchResult : documentSimilarityList) {
                            if (!myIDOfDocumentsSeen.contains(searchResult.second)) {
                                temporaryDocumentSimilarityList.add(searchResult);
                            }
                        }

                        documentSimilarityList = temporaryDocumentSimilarityList;
                    }
                    break;
                }

                default:
                    // this should never happen
                    documentSimilarityList = new ArrayList<>();
                    break;
            }

            // get all real id of search result, as it is needed in relevanceJudge.evaluate
            List<Integer> relevantDocuments = new ArrayList<>();
            for (int i = 0; i < documentSimilarityList.size(); i++) {
                Pair<Double, Integer> mSearchResult = documentSimilarityList.get(i);

                int docID = index.getIndexedDocument(mSearchResult.second).getId();

                relevantDocuments.add(docID);
            }

            // displayable result of our searching
            List<SearchResultEntry> searchResult = new ArrayList<>();
            for (int i = 0; i < documentSimilarityList.size(); i++) {
                Pair<Double, Integer> mSearchResult = documentSimilarityList.get(i);

                searchResult.add(new SearchResultEntry(
                        i + 1,
                        mSearchResult.first,
                        index.getIndexedDocument(mSearchResult.second)
                ));
            }

            // evaluate a query
            RelevanceJudge.Evaluation eval;
            if (experimentOption.isUseSameDocumentCollection()) {
                eval = relevanceJudge.evaluate(query.id, relevantDocuments);
            } else {
                eval = relevanceJudge.evaluate(query.id, relevantDocuments, filterIDList);
            }

            ExperimentResult experResult = new ExperimentResult(query.queryString, (double) eval.precision,
                    (double) eval.recall, (double) eval.nonInterpolatedPrecision, searchResult, initialQuery, secondQuery);
            experimentResultList.add(experResult);

            // writing table to file
            String line = eval.recall + "," + eval.precision + "," + eval.nonInterpolatedPrecision + "\n";
            writer.append(line);
        }

        writer.close();

        experimentResult = experimentResultList;
    }

    private int getSearcherV2ReweightMethod(RelevanceFeedbackOption option) {
        int reweightMethod = SearcherV2.ROCCHIO;
        switch (option) {
            case ROCCHIO:
                reweightMethod = SearcherV2.ROCCHIO;
                break;
            case IDE_REGULER:
                reweightMethod = SearcherV2.IDE;
                break;
            case IDE_DEC_HI:
                reweightMethod = SearcherV2.DEC_HI;
                break;
        }

        return reweightMethod;
    }

    public List<ExperimentResult> getExperimentResults() {
        return experimentResult;
    }

    public ExperimentResult getExperimentResult(int idx) {
        return experimentResult.get(idx);
    }

    private static final ApplicationLogic oneInstance = new ApplicationLogic();

    public static ApplicationLogic getInstance() {
        return oneInstance;
    }

    public void writeSummary() throws IOException {
        // START OF SETTING
        // Ubah settingan untuk 2 jenis Data dan Stemming atau tidak
        String queryPath = "dataset/ADI/query.text";
        String relevanceJudgementPath = "dataset/ADI/qrels.text";
        String documentLocation = "dataset/ADI/adi.all";
        // END OF SETTING

        // Relevance Judgement
        RelevanceJudge relevanceJudge = new RelevanceJudge(
                new File(queryPath),
                new File(relevanceJudgementPath));

        // Get experiment queries
        List<RelevanceJudge.Query> testQueries = relevanceJudge.getQueryList();

        File file = new File("relevanceSummary.csv");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);
        writer.write("");

        writer.append("\"Indexer TF Type\", \"Indexer Use IDF\", \"Indexer Use Norm\", \"Query TF Type\", " +
                "\"Query Use IDF\", \"Query Use Norm\", \"Use Stemming\", \"Precision Avg\", \"Recall Avg\", \"NonInterpolated Prec Avg\"\n");
        for (int useStemming = 0; useStemming < 2; useStemming++) {
            for (Calculator.TFType tfType : Calculator.TFType.values()) {
                for (int useIdf = 0; useIdf < 2; useIdf++) {
                    for (int useNorm = 0; useNorm < 2; useNorm++) {
                        // Indexing
                        this.indexDocuments(new File(documentLocation),
                                tfType, useIdf > 0, useNorm > 0, useStemming > 0);

                        for (Calculator.TFType queryTfType : Calculator.TFType.values()) {
                            for (int queryUseIdf = 0; queryUseIdf < 2; queryUseIdf++) {
                                for (int queryUseNorm = 0; queryUseNorm < 2; queryUseNorm++) {
                                    double recallSum = 0;
                                    double precisionSum = 0;
                                    double nonInterpolatedPrecSum = 0;


                                    // Iterate all query
                                    for (RelevanceJudge.Query query : testQueries) {
                                        // perform search on query
                                        Map<Term, Double> queryVectorSearcher = searcher.getQueryVector(this.index,
                                                query.queryString,
                                                this.stopwords,
                                                queryTfType,
                                                queryUseIdf > 0,
                                                queryUseNorm > 0,
                                                useStemming > 0);
                                        List<Pair<Double, Integer>> documentSimilarityList =
                                                this.searcher.search(
                                                        this.index,
                                                        queryVectorSearcher);

                                        // Get all document id of search result, as it is needed
                                        List<Integer> relevantDocuments = new ArrayList<>();
                                        List<SearchResultEntry> searchResult = new ArrayList<>();
                                        for (int i = 0; i < documentSimilarityList.size(); i++) {
                                            Pair<Double, Integer> documentSimilarity = documentSimilarityList.get(i);
                                            int docID = this.index.getIndexedDocument(documentSimilarity.second).getId();
                                            relevantDocuments.add(docID);
                                            searchResult.add(new SearchResultEntry(
                                                    i + 1,
                                                    documentSimilarity.first,
                                                    this.index.getIndexedDocument(documentSimilarity.second)
                                            ));
                                        }

                                        // Evaluate a query
                                        RelevanceJudge.Evaluation eval = relevanceJudge.evaluate(query.id, relevantDocuments);
                                        precisionSum += (double) eval.precision;
                                        recallSum += (double) eval.recall;
                                        nonInterpolatedPrecSum += (double) eval.nonInterpolatedPrecision;

                                    }
                                    String line = "\"" + tfType.name() + "\"" + "," + useIdf + "," + useNorm + "," + queryTfType.name() + "," + queryUseIdf + "," + queryUseNorm + "," +
                                            useStemming + "," + precisionSum / testQueries.size() + "," + recallSum / testQueries.size() + "," +
                                            nonInterpolatedPrecSum / testQueries.size() + "\n";
                                    writer.append(line);
                                }
                            }
                        }


                    }
                }
            }
        }

        writer.close();
    }
}
