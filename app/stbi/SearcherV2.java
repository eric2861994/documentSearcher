package stbi;

import stbi.common.IndexedDocument;
import stbi.common.index.Index;
import stbi.common.term.Term;
import stbi.common.util.Pair;
import stbi.feedback.RelevanceFeedbackUtils;
import stbi.searcher.Searcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearcherV2 {
    private final Index index;
    private final Searcher searcher;


    private int documentsToPresent = 1;
    private int reweightMethod = ROCCHIO;
    private boolean doExpansion = true;


    Map<Integer, Set<Integer>> relevanceJudgement;

    public SearcherV2(Index a_index, Searcher a_searcher) {
        index = a_index;
        searcher = a_searcher;
    }

    public List<Pair<Double, Integer>> relevanceFeedbackExperiment(int queryID) {
        Map<Term, Double> initialQuery = searcher.getQueryVector(index, "", null, null, false, false, false);
        List<Pair<Double, Integer>> searchResult = searcher.search(null, null);

        int toTake = Math.min(documentsToPresent, searchResult.size());
        if (toTake > 0) {
            List<Integer> topDocuments = takeTopDocuments(searchResult, toTake);
            Set<Integer> relevantDocumentSet = relevanceJudgement.get(queryID);
            List<Map<Term, Double>> relevantVectors = getDocumentVectors(topDocuments, relevantDocumentSet, true);
            List<Map<Term, Double>> irrelevantVectors = getDocumentVectors(topDocuments, relevantDocumentSet, false);

            Map<Term, Double> reweightedQuery = reweightQuery(initialQuery, relevantVectors, irrelevantVectors);
            return searcher.search(index, reweightedQuery);

        } else {
            return searchResult;
        }
    }

    private List<Map<Term, Double>> getDocumentVectors(List<Integer> topDocuments, Set<Integer> relevantDocumentSet, boolean value) {
        List<Map<Term, Double>> relevantVectors = new ArrayList<>();

        for (Integer topDocumentsIndexId : topDocuments) {
            IndexedDocument document = index.getIndexedDocument(topDocumentsIndexId);

            // jika id sebenarnya berada di dalam set
            if (relevantDocumentSet.contains(document.getId()) == value) {
                // tambahkan term vector dokumen tersebut
                relevantVectors.add(index.getDocumentTermVector(topDocumentsIndexId));
            }
        }

        return relevantVectors;
    }

    private Map<Term, Double> reweightQuery(Map<Term, Double> initialQuery, List<Map<Term,Double>> relevantVectors,
                                            List<Map<Term,Double>> irrelevantVectors) {
        switch (reweightMethod) {
            case ROCCHIO:
                return RelevanceFeedbackUtils.rochioQueryReweighting(initialQuery, relevantVectors, irrelevantVectors, doExpansion);
            case IDE:
                return RelevanceFeedbackUtils.ideQueryReweighting(initialQuery, relevantVectors, irrelevantVectors, doExpansion);
            case DEC_HI:
                return RelevanceFeedbackUtils.deHaiQueryReweighting(initialQuery, relevantVectors, irrelevantVectors.get(0), doExpansion);
        }
        // ASSUME this never happen
        return null;
    }

    private List<Integer> takeTopDocuments(List<Pair<Double, Integer>> searchResult, int toTake) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < toTake; i++) {
            Pair<Double, Integer> searchEntry = searchResult.get(i);

            result.add(searchEntry.second);
        }

        return result;
    }

    public int getReweightMethod() {
        return reweightMethod;
    }

    public void setReweightMethod(int reweightMethod) {
        this.reweightMethod = reweightMethod;
    }

    public static final int ROCCHIO = 0, IDE = 1, DEC_HI = 2;
}
