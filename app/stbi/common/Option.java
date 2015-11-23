package stbi.common;

import stbi.common.util.Calculator;
import stbi.common.util.RelevanceFeedbackOption;
import stbi.common.util.RelevanceFeedbackStatus;

/**
 * Search/Indexing options.
 */
public class Option {
    private Calculator.TFType tfType;
    private boolean useIDF;
    private boolean useNormalization;
    private boolean useStemmer;
    private RelevanceFeedbackStatus relevanceFeedbackStatus;
    private RelevanceFeedbackOption relevanceFeedbackOption;
    private boolean useSameDocumentCollection;
    private int S;
    private int N;
    private boolean useQueryExpansion;

    public Option() {
        this(Calculator.TFType.RAW_TF, false, false, false, RelevanceFeedbackStatus.NO_RELEVANCE_FEEDBACK, RelevanceFeedbackOption.ROCCHIO, false, 0, 0, false);
    }

    public Option(Calculator.TFType _tfType, boolean _useIDF, boolean _useNormalization, boolean _useStemmer, RelevanceFeedbackStatus relevanceFeedbackStatus, RelevanceFeedbackOption relevanceFeedbackOption, boolean useSameDocumentCollection, int S, int N, boolean useQueryExpansion) {
        setTfType(_tfType);
        setUseIDF(_useIDF);
        setUseNormalization(_useNormalization);
        setUseStemmer(_useStemmer);
        setRelevanceFeedbackStatus(relevanceFeedbackStatus);
        setUseSameDocumentCollection(useSameDocumentCollection);
        setS(S);
        setN(N);
        setUseQueryExpansion(useQueryExpansion);
        setRelevanceFeedbackOption(relevanceFeedbackOption);
    }

    public Calculator.TFType getTfType() {
        return tfType;
    }

    public void setTfType(Calculator.TFType tfType) {
        this.tfType = tfType;
    }

    public boolean isUseIDF() {
        return useIDF;
    }

    public void setUseIDF(boolean useIDF) {
        this.useIDF = useIDF;
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
