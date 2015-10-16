package formstubs;

import play.data.validation.Constraints;
import stbi.common.util.Calculator;

/**
 * Created by nim_13512065 on 10/15/15.
 */
public class IndexingDocumentStub extends StopwordsStub {
    private String documentLocation;

    private boolean useIdf;
    @Constraints.Required
    private Calculator.TFType tf;
    private boolean useNormalization;
    private boolean useStemmer;

    private boolean useStopwords;

    //DONT REMOVE THIS CONSTRUCTOR
    public IndexingDocumentStub() {
        super();
    }

    public IndexingDocumentStub(String documentLocation, String stopwordLocation, Calculator.TFType tfType, boolean dIdf, boolean dNormalization, boolean dStemmer) {
        setDocumentLocation(documentLocation);
        setStopwordLocation(stopwordLocation);
        setUseIdf(dIdf);
        setTf(tfType);
        setUseNormalization(dNormalization);
        setUseStemmer(dStemmer);
    }

    public String getDocumentLocation() {
        return documentLocation;
    }

    public void setDocumentLocation(String documentLocation) {
        this.documentLocation = documentLocation;
    }

    public boolean isUseIdf() {
        return useIdf;
    }

    public void setUseIdf(boolean useIdf) {
        this.useIdf = useIdf;
    }

    public Calculator.TFType getTf() {
        return tf;
    }

    public void setTf(Calculator.TFType tf) {
        this.tf = tf;
    }

    public boolean isUseNormalization() {
        return useNormalization;
    }

    public void setUseNormalization(boolean useNormalization) {
        this.useNormalization = useNormalization;
    }

    public boolean isUseStemmer() {
        return useStemmer;
    }

    public void setUseStemmer(boolean useStemmer) {
        this.useStemmer = useStemmer;
    }

    public boolean isUseStopwords() {
        return useStopwords;
    }

    public void setUseStopwords(boolean useStopwords) {
        this.useStopwords = useStopwords;
    }
}
