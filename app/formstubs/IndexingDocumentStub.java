package formstubs;

import play.data.validation.Constraints;
import stbi.common.util.Calculator;

/**
 * Created by nim_13512065 on 10/15/15.
 */
public class IndexingDocumentStub {
    private String documentLocation;
    private String stopwordLocation;

    private boolean dIdf;
    @Constraints.Required
    private Calculator.TFType dTf;
    private boolean dNormalization;
    private boolean dStemmer;

    //DONT REMOVE THIS CONSTRUCTOR
    public IndexingDocumentStub() {

    }

    public IndexingDocumentStub(String documentLocation, String stopwordLocation, Calculator.TFType tfType, boolean dIdf, boolean dNormalization, boolean dStemmer) {
        setDocumentLocation(documentLocation);
        setStopwordLocation(stopwordLocation);
        setdIdf(dIdf);
        setdTf(tfType);
        setdNormalization(dNormalization);
        setdStemmer(dStemmer);
    }

    public String getDocumentLocation() {
        return documentLocation;
    }

    public void setDocumentLocation(String documentLocation) {
        this.documentLocation = documentLocation;
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

    public boolean isdStemmer() {
        return dStemmer;
    }

    public void setdStemmer(boolean dStemmer) {
        this.dStemmer = dStemmer;
    }
}
