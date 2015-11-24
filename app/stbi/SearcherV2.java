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

    public SearcherV2(Index a_index, Searcher a_searcher) {
        index = a_index;
        searcher = a_searcher;
    }

    public Map<Term, Double> relevanceFeedback(
            Map<Term, Double> initialQuery, List<Pair<Double, Integer>> searchResult, Set<Integer> relevantDocumentSet,
            int documentsToPresent, int reweightMethod, boolean doExpansion) {

        List<Integer> topDocuments = takeTopDocuments(searchResult, documentsToPresent);
        if (topDocuments.size() > 0) {
            List<Map<Term, Double>> relevantVectors = getDocumentVectors(topDocuments, relevantDocumentSet, true);
            List<Map<Term, Double>> irrelevantVectors = getDocumentVectors(topDocuments, relevantDocumentSet, false);

            return reweightQuery(initialQuery, relevantVectors, irrelevantVectors, reweightMethod, doExpansion);

        } else {
            return initialQuery;
        }
    }

    public Map<Term, Double> relevanceFeedbackV2(
            Map<Term, Double> initialQuery, List<Map<Term, Double>> relevantVectors,
            List<Map<Term, Double>> irrelevantVectors, int reweightMethod, boolean doExpansion) {

        return reweightQuery(initialQuery, relevantVectors, irrelevantVectors, reweightMethod, doExpansion);
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

    private Map<Term, Double> reweightQuery(Map<Term, Double> initialQuery, List<Map<Term, Double>> relevantVectors,
                                            List<Map<Term, Double>> irrelevantVectors, int reweightMethod, boolean doExpansion) {
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

    public List<Integer> takeTopDocuments(List<Pair<Double, Integer>> searchResult, int toTake) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < toTake && i < searchResult.size(); i++) {
            Pair<Double, Integer> searchEntry = searchResult.get(i);

            result.add(searchEntry.second);
        }

        return result;
    }

    public static final int ROCCHIO = 0, IDE = 1, DEC_HI = 2;
}
