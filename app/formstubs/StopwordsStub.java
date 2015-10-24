package formstubs;

public class StopwordsStub {
    private String stopwordLocation;

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
