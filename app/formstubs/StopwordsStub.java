package formstubs;

/**
 * Created by nim_13512065 on 10/16/15.
 */
public class StopwordsStub {
    protected String stopwordLocation;
    public StopwordsStub() {

    }

    public StopwordsStub(StopwordsStub stopwordsStub) {
        setStopwordLocation(stopwordsStub.getStopwordLocation());
    }

    public String getStopwordLocation() {
        return stopwordLocation;
    }

    public void setStopwordLocation(String stopwordLocation) {
        this.stopwordLocation = stopwordLocation;
    }
}
