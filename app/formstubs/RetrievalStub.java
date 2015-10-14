package formstubs;

import stbi.common.util.Calculator;

/**
 * Created by nim_13512065 on 10/15/15.
 */
public class RetrievalStub {
    protected boolean qIdf;
    protected Calculator.TFType qTf;
    protected boolean qNormalization;
    protected boolean qStemmer;

    public RetrievalStub() {

    }

    public RetrievalStub(RetrievalStub retrievalStub){
        setqIdf(retrievalStub.isqIdf());
        setqTf(retrievalStub.getqTf());
        setqNormalization(retrievalStub.isqNormalization());
        setqStemmer(retrievalStub.isqStemmer());
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

    public boolean isqStemmer() {
        return qStemmer;
    }

    public void setqStemmer(boolean qStemmer) {
        this.qStemmer = qStemmer;
    }
}
