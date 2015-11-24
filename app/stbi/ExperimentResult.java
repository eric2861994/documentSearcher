package stbi;

import stbi.common.term.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperimentResult {
    private List<SearchResultEntry> experimentalDetailResultList;
    private double precision;
    private double recall;
    private double noninterpolatedAveragePrecision;
    private String query;
    private Map<Term, Double> initialQuery;
    private Map<Term, Double> secondQuery;

    public ExperimentResult() {
    }

    public ExperimentResult(String query, double precision, double recall, double a_noninterpolatedAveragePrecision,
                            List<SearchResultEntry> experimentalDetailResultList, Map<Term, Double> initialQuery, Map<Term, Double> secondQuery) {
        setQuery(query);
        setPrecision(precision);
        setRecall(recall);
        noninterpolatedAveragePrecision = a_noninterpolatedAveragePrecision;
        setExperimentalDetailResultList(new ArrayList<>(experimentalDetailResultList));
        setInitialQuery(initialQuery);
        if (secondQuery == null) {
            setSecondQuery(new HashMap<Term, Double>());
        } else {
            setSecondQuery(secondQuery);
        }
    }

    public final double getPrecision() {
        return precision;
    }

    public final void setPrecision(double precision) {
        this.precision = precision;
    }

    public final double getRecall() {
        return recall;
    }

    public final void setRecall(double recall) {
        this.recall = recall;
    }

    public final String getQuery() {
        return query;
    }

    public final void setQuery(String query) {
        this.query = query;
    }

    public final List<SearchResultEntry> getExperimentalDetailResultList() {
        return experimentalDetailResultList;
    }

    public final void setExperimentalDetailResultList(List<SearchResultEntry> experimentalDetailResultList) {
        this.experimentalDetailResultList = experimentalDetailResultList;
    }

    public double getNoninterpolatedAveragePrecision() {
        return noninterpolatedAveragePrecision;
    }

    public Map<Term, Double> getInitialQuery() {
        return initialQuery;
    }

    public void setInitialQuery(Map<Term, Double> initialQuery) {
        this.initialQuery = initialQuery;
    }

    public Map<Term, Double> getSecondQuery() {
        return secondQuery;
    }

    public void setSecondQuery(Map<Term, Double> secondQuery) {
        this.secondQuery = secondQuery;
    }
}
