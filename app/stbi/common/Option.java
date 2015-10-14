package stbi.common;

import stbi.common.util.Calculator;

/**
 * Search/Indexing options.
 */
public class Option {
    private Calculator.TFType tfType;
    private boolean useIDF;
    private boolean useNormalization;
    private boolean useStemmer;

    public Option(Calculator.TFType _tfType, boolean _useIDF, boolean _useNormalization, boolean _useStemmer) {
        setTfType(_tfType);
        setUseIDF(_useIDF);
        setUseNormalization(_useNormalization);
        setUseStemmer(_useStemmer);
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
}
