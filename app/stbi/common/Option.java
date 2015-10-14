package stbi.common;

import stbi.common.util.Calculator;

/**
 * Search/Indexing options.
 */
public class Option {
    public Calculator.TFType tfType;
    public boolean useIDF;
    public boolean useNormalization;
    public boolean useStemmer;

    public Option(Calculator.TFType _tfType, boolean _useIDF, boolean _useNormalization, boolean _useStemmer) {
        tfType = _tfType;
        useIDF = _useIDF;
        useNormalization = _useNormalization;
        useStemmer = _useStemmer;
    }
}
