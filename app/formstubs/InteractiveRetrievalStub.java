package formstubs;

/**
 * Created by nim_13512065 on 10/15/15.
 */
public class InteractiveRetrievalStub extends RetrievalStub {
    protected String query;
    protected String stopwordLocation;

    public InteractiveRetrievalStub() {
        super();
    }

    public InteractiveRetrievalStub(InteractiveRetrievalStub interactiveRetrievalStub) {
        super(interactiveRetrievalStub);
        setQuery(interactiveRetrievalStub.getQuery());
        setStopwordLocation(interactiveRetrievalStub.getStopwordLocation());
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
