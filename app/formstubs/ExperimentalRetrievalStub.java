package formstubs;

public class ExperimentalRetrievalStub extends RetrievalStub {
    private String queryLocation;
    private String relevantJudgementLocation;

    public ExperimentalRetrievalStub() {
        queryLocation = "";
        relevantJudgementLocation = "";
    }

    public ExperimentalRetrievalStub(ExperimentalRetrievalStub experimentalRetrievalStub) {
        super(experimentalRetrievalStub);
        setQueryLocation(experimentalRetrievalStub.getQueryLocation());
        setRelevantJudgementLocation(experimentalRetrievalStub.getRelevantJudgementLocation());
    }

    public String getQueryLocation() {
        return queryLocation;
    }

    public void setQueryLocation(String query) {
        queryLocation = query;
    }

    public String getRelevantJudgementLocation() {
        return relevantJudgementLocation;
    }

    public void setRelevantJudgementLocation(String relevantJudgementLocation) {
        this.relevantJudgementLocation = relevantJudgementLocation;
    }
}
