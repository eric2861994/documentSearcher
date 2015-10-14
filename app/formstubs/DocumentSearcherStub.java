package formstubs;

import stbi.common.util.Calculator;

/**
 * Created by nim_13512065 on 10/10/15.
 */
public class DocumentSearcherStub {
    private String documentLocation;
    private String queryLocation;
    private String relevantJudgement;
    private String stopwordLocation;

    private boolean dIdf;
    private Calculator.TFType dTf;
    private boolean dNormalization;

    private boolean qIdf;
    private Calculator.TFType qTf;
    private boolean qNormalization;

    public String getDocumentLocation() {
        return documentLocation;
    }

    public void setDocumentLocation(String documentLocation) {
        this.documentLocation = documentLocation;
    }

    public String getQueryLocation() {
        return queryLocation;
    }

    public void setQueryLocation(String queryLocation) {
        this.queryLocation = queryLocation;
    }

    public String getRelevantJudgement() {
        return relevantJudgement;
    }

    public void setRelevantJudgement(String relevantJudgement) {
        this.relevantJudgement = relevantJudgement;
    }

    public String getStopwordLocation() {
        return stopwordLocation;
    }

    public void setStopwordLocation(String stopwordLocation) {
        this.stopwordLocation = stopwordLocation;
    }

    public boolean isdIdf() {
        return dIdf;
    }

    public void setdIdf(boolean dIdf) {
        this.dIdf = dIdf;
    }

    public Calculator.TFType getdTf() {
        return dTf;
    }

    public void setdTf(Calculator.TFType dTf) {
        this.dTf = dTf;
    }

    public boolean isdNormalization() {
        return dNormalization;
    }

    public void setdNormalization(boolean dNormalization) {
        this.dNormalization = dNormalization;
    }

    public boolean isqIdf() {
        return qIdf;
    }

    public void setqIdf(boolean qIdf) {
        this.qIdf = qIdf;
    }

    public Calculator.TFType getqTf() {
        return qTf;
    }

    public void setqTf(Calculator.TFType qTf) {
        this.qTf = qTf;
    }

    public boolean isqNormalization() {
        return qNormalization;
    }

    public void setqNormalization(boolean qNormalization) {
        this.qNormalization = qNormalization;
    }
}
