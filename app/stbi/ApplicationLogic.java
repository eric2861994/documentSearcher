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

    private ApplicationLogic() {
        indexFile = Play.application().getFile(INDEX_FILE_PATH);
        readableIndexFile = Play.application().getFile(INDEX_FILE_PATH+"csv");
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

    public IndexedDocument getIndexedDocument(int docID) {
        return index.getIndexedDocument(docID);
    }

    public List<Pair<Double, Integer>> searchQuery(String query) throws IndexedFileException {
        if (stopwords == null || index == null) {
            throw new IndexedFileException();
        }

        Option searchOption = getSearchOption();
        Map<Term, Double> queryVectorSearcher = searcher.getQueryVector(index, query, stopwords, searchOption.getTfType(), searchOption.isUseIDF(),
                searchOption.isUseNormalization(), searchOption.isUseStemmer());
        return searcher.search(index, queryVectorSearcher);
    }

    public void saveIndexingSettings(IndexingDocumentStub indexingDocumentStub) throws IOException {
        new ObjectMapper().writeValue(indexSettingFile, indexingDocumentStub);
    }

    public void saveStopwordsSetting(StopwordsStub stopwordsStub) throws IOException {
        File stopwordFile = new File(stopwordsStub.getStopwordLocation());
        stopwords = loader.loadStopwords(stopwordFile);
        new ObjectMapper().writeValue(stopwordSettingFile, stopwordsStub);
    }

    public void performExperiment(String queryPath, String relevanceJudgementPath) throws IOException {
        RelevanceJudge relevanceJudge = new RelevanceJudge(
                Play.application().getFile(queryPath),
                Play.application().getFile(relevanceJudgementPath)
        );

        File file = new File(getExperimentOption().getTfType()+"-"+getExperimentOption().isUseIDF()+"-"+getExperimentOption().isUseNormalization()+"-"+getExperimentOption().isUseStemmer()+".csv");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file);
        writer.write("");

        // get experiment queries
        List<RelevanceJudge.Query> testQueries = relevanceJudge.getQueryList();

        List<ExperimentResult> experimentResultList = new ArrayList<>();
        for (RelevanceJudge.Query query : testQueries) {
            // perform search on query
            Option option = getExperimentOption();
            Map<Term, Double> queryVectorSearcher = searcher.getQueryVector(index, query.queryString, stopwords,
                    option.getTfType(), option.isUseIDF(), option.isUseNormalization(), option.isUseStemmer());
            List<Pair<Double, Integer>> documentSimilarityList = searcher.search(index, queryVectorSearcher);

            // get all document id of search result, as it is needed in relevanceJudge.evaluate
            List<Integer> relevantDocuments = new ArrayList<>();
            List<SearchResultEntry> searchResult = new ArrayList<>();
            for (int i = 0; i < documentSimilarityList.size(); i++) {
                Pair<Double, Integer> documentSimilarity = documentSimilarityList.get(i);
                int docID = index.getIndexedDocument(documentSimilarity.second).getId();
                relevantDocuments.add(docID);
                searchResult.add(new SearchResultEntry(
                        i + 1,
                        documentSimilarity.first,
                        index.getIndexedDocument(documentSimilarity.second)
                ));

            }

            // evaluate a query
            RelevanceJudge.Evaluation eval = relevanceJudge.evaluate(query.id, relevantDocuments);

            ExperimentResult experResult = new ExperimentResult(query.queryString,
                    (double) eval.precision, (double) eval.recall, (double) eval.nonInterpolatedPrecision, searchResult);
            experimentResultList.add(experResult);

            String line = eval.recall + "," + eval.precision + "," + eval.nonInterpolatedPrecision + "\n";
            writer.append(line);
        }

        writer.close();

        experimentResult = experimentResultList;
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

    public void writeSummary () throws IOException {
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
        for(int useStemming=0; useStemming <2; useStemming++){
            for(Calculator.TFType tfType : Calculator.TFType.values()){
                for(int useIdf=0; useIdf<2; useIdf++){
                    for(int useNorm=0; useNorm<2; useNorm++){
                        // Indexing
                        this.indexDocuments(new File(documentLocation),
                                tfType, useIdf > 0, useNorm > 0, useStemming > 0);

                        for(Calculator.TFType queryTfType : Calculator.TFType.values() ){
                            for(int queryUseIdf=0; queryUseIdf <2; queryUseIdf++){
                                for(int queryUseNorm=0; queryUseNorm <2; queryUseNorm++){
                                    double recallSum = 0;
                                    double precisionSum = 0;
                                    double nonInterpolatedPrecSum=0;


                                    // Iterate all query
                                    for(RelevanceJudge.Query query : testQueries){
                                        // perform search on query
                                        Map<Term, Double> queryVectorSearcher = searcher.getQueryVector(                                                        this.index,
                                                query.queryString,
                                                this.stopwords,
                                                queryTfType,
                                                queryUseIdf>0,
                                                queryUseNorm>0,
                                                useStemming>0);
                                        List<Pair<Double, Integer>> documentSimilarityList =
                                                this.searcher.search(
                                                        this.index,
                                                        queryVectorSearcher);

                                        // Get all document id of search result, as it is needed
                                        List<Integer> relevantDocuments = new ArrayList<>();
                                        List<SearchResultEntry> searchResult = new ArrayList<>();
                                        for(int i=0; i< documentSimilarityList.size(); i++){
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
                                    String line = "\""+ tfType.name()+"\""+","+ useIdf +","+ useNorm +","+ queryTfType.name() +","+ queryUseIdf +","+ queryUseNorm +","+
                                            useStemming +","+ precisionSum/testQueries.size() +","+ recallSum/testQueries.size() +","+
                                            nonInterpolatedPrecSum/testQueries.size() + "\n";
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
