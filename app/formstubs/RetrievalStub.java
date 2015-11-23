package formstubs;

import stbi.common.util.Calculator;
import stbi.common.util.RelevanceFeedbackOption;
import stbi.common.util.RelevanceFeedbackStatus;

public class RetrievalStub extends StopwordsStub {
    protected boolean useIdf;
    protected Calculator.TFType tf;
    protected boolean useNormalization;
    protected boolean useStemmer;
    protected boolean useStopwords;
    protected RelevanceFeedbackStatus relevanceFeedbackStatus;
    private RelevanceFeedbackOption relevanceFeedbackOption;
    protected boolean useSameDocumentCollection;
    protected int S;
    protected int N;
    protected boolean useQueryExpansion;

    public RetrievalStub() {
        super();
        setTf(Calculator.TFType.RAW_TF);
        setRelevanceFeedbackStatus(RelevanceFeedbackStatus.NO_RELEVANCE_FEEDBACK);
    }

    public RetrievalStub(RetrievalStub retrievalStub) {
        super(retrievalStub);
        setUseIdf(retrievalStub.isUseIdf());
        setTf(retrievalStub.getTf());
        setUseNormalization(retrievalStub.isUseNormalization());
        setUseStemmer(retrievalStub.isUseStemmer());
        setRelevanceFeedbackStatus(retrievalStub.getRelevanceFeedbackStatus());
        setUseSameDocumentCollection(retrievalStub.isUseSameDocumentCollection());
        setS(retrievalStub.getS());
        setN(retrievalStub.getN());
        setUseQueryExpansion(retrievalStub.isUseQueryExpansion());
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

    public RelevanceFeedbackStatus getRelevanceFeedbackStatus() {
        return relevanceFeedbackStatus;
    }

    public void setRelevanceFeedbackStatus(RelevanceFeedbackStatus relevanceFeedbackStatus) {
        this.relevanceFeedbackStatus = relevanceFeedbackStatus;
    }

    public boolean isUseSameDocumentCollection() {
        return useSameDocumentCollection;
    }

    public void setUseSameDocumentCollection(boolean useSameDocumentCollection) {
        this.useSameDocumentCollection = useSameDocumentCollection;
    }

    public int getS() {
        return S;
    }

    public void setS(int s) {
        S = s;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public boolean isUseQueryExpansion() {
        return useQueryExpansion;
    }

    public void setUseQueryExpansion(boolean useQueryExpansion) {
        this.useQueryExpansion = useQueryExpansion;
    }

    public RelevanceFeedbackOption getRelevanceFeedbackOption() {
        return relevanceFeedbackOption;
    }

    public void setRelevanceFeedbackOption(RelevanceFeedbackOption relevanceFeedbackOption) {
        this.relevanceFeedbackOption = relevanceFeedbackOption;
    }
}
