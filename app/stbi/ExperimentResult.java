package stbi;

import java.util.ArrayList;
import java.util.List;

public class ExperimentResult {
    private List<SearchResultEntry> experimentalDetailResultList;
    private double precision;
    private double recall;
    private double noninterpolatedAveragePrecision;
    private String query;

    public ExperimentResult() {
    }

    public ExperimentResult(String query, Double precision, Double recall, List<SearchResultEntry> experimentalDetailResultList) {
        setPrecision(precision);
        setRecall(recall);
        setQuery(query);
        setExperimentalDetailResultList(new ArrayList<SearchResultEntry>(experimentalDetailResultList));
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
}
