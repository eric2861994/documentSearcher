package formstubs;

public class InteractiveRetrievalStub extends RetrievalStub {
    protected String query;
    private boolean useStopwords;

    public InteractiveRetrievalStub() {
        super();
    }

    public InteractiveRetrievalStub(InteractiveRetrievalStub interactiveRetrievalStub) {
        super(interactiveRetrievalStub);
        setQuery(interactiveRetrievalStub.getQuery());
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
}
