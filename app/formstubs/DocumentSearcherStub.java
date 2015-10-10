package formstubs;

import formstubs.options.IDFOptions;
import formstubs.options.NormalizationOptions;
import formstubs.options.TFOptions;
import play.data.validation.Constraints.Required;

/**
 * Created by nim_13512065 on 10/10/15.
 */
public class DocumentSearcherStub {
    private String documentLocation;
    private String queryLocation;
    private String relevantJudgement;
    private String stopwordLocation;

    private IDFOptions dIdf;
    private TFOptions dTf;
    private NormalizationOptions dNormalization;

    private IDFOptions qIdf;
    private TFOptions qTf;
    private NormalizationOptions qNormalization;
}
