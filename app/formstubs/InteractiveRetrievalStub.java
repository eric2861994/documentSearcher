package formstubs;

/**
 * Created by nim_13512065 on 10/15/15.
 */
public class InteractiveRetrievalStub extends RetrievalStub {
    protected String query;

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

}
