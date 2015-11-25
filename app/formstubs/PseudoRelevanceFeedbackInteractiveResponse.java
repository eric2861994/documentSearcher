package formstubs;

import stbi.common.IndexedDocument;
import stbi.common.term.Term;
import stbi.common.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by nim_13512065 on 11/25/15.
 */
public class PseudoRelevanceFeedbackInteractiveResponse {
    private List<Pair<Double, IndexedDocument>> hasilPencarian;
    private Map<Term, Double> queryLama;

    private Map<Term, Double> queryBaru;

    public Map<Term, Double> getQueryBaru() {
        return queryBaru;
    }

    public void setQueryBaru(Map<Term, Double> queryBaru) {
        this.queryBaru = queryBaru;
    }

    public List<Pair<Double, IndexedDocument>> getHasilPencarian() {
        return hasilPencarian;
    }

    public void setHasilPencarian(List<Pair<Double, IndexedDocument>> hasilPencarian) {
        this.hasilPencarian = hasilPencarian;
    }

    public Map<Term, Double> getQueryLama() {
        return queryLama;
    }

    public void setQueryLama(Map<Term, Double> queryLama) {
        this.queryLama = queryLama;
    }
}
