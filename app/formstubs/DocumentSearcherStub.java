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
}
