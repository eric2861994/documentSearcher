package formstubs;

/**
 * Created by nim_13512065 on 10/15/15.
 */
public class ExperimentalRetrievalStub extends RetrievalStub {
    protected String query;
    protected String stopwordLocation;

    public ExperimentalRetrievalStub() {
        super();
    }

    public ExperimentalRetrievalStub(ExperimentalRetrievalStub experimentalRetrievalStub) {
        super(experimentalRetrievalStub);
        setQuery(experimentalRetrievalStub.getQuery());
        setStopwordLocation(experimentalRetrievalStub.getStopwordLocation());
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getStopwordLocation() {
        return stopwordLocation;
    }

    public void setStopwordLocation(String stopwordLocation) {
        this.stopwordLocation = stopwordLocation;
    }
}
