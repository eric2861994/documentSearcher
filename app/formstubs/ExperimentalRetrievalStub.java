package formstubs;

/**
 * Created by nim_13512065 on 10/16/15.
 */
public class ExperimentalRetrievalStub extends RetrievalStub {
    protected String query;
    private boolean useStopwords;
    private String relevantJudgementLocation;

    public ExperimentalRetrievalStub() {
        super();
    }

    public ExperimentalRetrievalStub(ExperimentalRetrievalStub experimentalRetrievalStub) {
        super(experimentalRetrievalStub);
        setQuery(experimentalRetrievalStub.getQuery());
        setRelevantJudgementLocation(experimentalRetrievalStub.getRelevantJudgementLocation());
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isUseStopwords() {
        return useStopwords;
    }

    public void setUseStopwords(boolean useStopwords) {
        this.useStopwords = useStopwords;
    }

    public String getRelevantJudgementLocation() {
        return relevantJudgementLocation;
    }

    public void setRelevantJudgementLocation(String relevantJudgementLocation) {
        this.relevantJudgementLocation = relevantJudgementLocation;
    }
}
