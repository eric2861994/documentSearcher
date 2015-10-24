package formstubs;

import stbi.common.util.Calculator;

public class RetrievalStub extends StopwordsStub {
    protected boolean useIdf;
    protected Calculator.TFType tf;
    protected boolean useNormalization;
    protected boolean useStemmer;
    protected boolean useStopwords;

    public RetrievalStub() {
        super();
        setTf(Calculator.TFType.RAW_TF);
    }

    public RetrievalStub(RetrievalStub retrievalStub) {
        super(retrievalStub);
        setUseIdf(retrievalStub.isUseIdf());
        setTf(retrievalStub.getTf());
        setUseNormalization(retrievalStub.isUseNormalization());
        setUseStemmer(retrievalStub.isUseStemmer());
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
}
