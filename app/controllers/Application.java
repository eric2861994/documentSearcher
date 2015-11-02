package controllers;

import formstubs.ExperimentalRetrievalStub;
import formstubs.IndexingDocumentStub;
import formstubs.InteractiveRetrievalStub;
import formstubs.StopwordsStub;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import stbi.ApplicationLogic;
import stbi.IndexedFileException;
import stbi.common.IndexedDocument;
import stbi.common.Option;
import stbi.common.util.Calculator;
import stbi.common.util.Pair;
import views.html.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {
    private static final String DOCUMENT_INDEXING_TITLE = "Document Indexing";
    private static final String EXPERIMENTAL_TITLE = "Experimental";
    private static final String INTERACTIVE_TITLE = "Interactive";
    private static final String STOPWORDS_TITLE = "Stopwords";

    public static final ApplicationLogic appLogic = ApplicationLogic.getInstance();

    public static Result index() {
        return redirect("/indexing");
    }

    public static Result stopwords() {
        StopwordsStub stopwordsStub = appLogic.loadStopwordSetting();
        Form<StopwordsStub> interactiveRetrievalStubForm = Form.form(StopwordsStub.class);
        if (stopwordsStub != null) {
            interactiveRetrievalStubForm = interactiveRetrievalStubForm.fill(stopwordsStub);
        }

        return ok(stopwords.render(STOPWORDS_TITLE, interactiveRetrievalStubForm));
    }

    public static Result postStopwords() {
        Form<StopwordsStub> stopwordsStubForm = Form.form(StopwordsStub.class);
        StopwordsStub stopwordsStub = stopwordsStubForm.bindFromRequest().get();

        try {
            appLogic.saveStopwordsSetting(stopwordsStub);
            flash("success", "The stopwords has been saved");
            return redirect("/indexing");

        } catch (IOException e) {
            e.printStackTrace();
            flash("error", "The stopwords cannot be saved");
            return badRequest();
        }
    }

    public static Result indexingDocument() throws IOException {
        boolean indexLoaded = appLogic.indexLoaded();
        if (indexLoaded) {
            flash("info", "The index already loaded");
        } else {
            flash("info", "The index is not yet created");
        }

        IndexingDocumentStub currentFormEntries = appLogic.loadIndexingSettings();
        Form<IndexingDocumentStub> indexingForm = Form.form(IndexingDocumentStub.class).fill(currentFormEntries);

        return ok(index.render(DOCUMENT_INDEXING_TITLE, indexingForm));
    }

    public static Result postIndexingDocument() {
        Form<IndexingDocumentStub> indexingDocumentStubForm = Form.form(IndexingDocumentStub.class);
        IndexingDocumentStub indexingDocumentStub = indexingDocumentStubForm.bindFromRequest().get();

        String documentLocation = indexingDocumentStub.getDocumentLocation();
        Calculator.TFType tfType = indexingDocumentStub.getTf();
        boolean dIdf = indexingDocumentStub.isUseIdf();
        boolean dNormalization = indexingDocumentStub.isUseNormalization();
        boolean dStemmer = indexingDocumentStub.isUseStemmer();

        File documentsFile = Play.application().getFile(documentLocation);

        try {
            appLogic.indexDocuments(documentsFile, tfType, dIdf, dNormalization, dStemmer);
            appLogic.saveIndexingSettings(indexingDocumentStub);
            flash("success", "The file has been created");
        } catch (IOException e) {
            flash("error", "The file cannot be created " + "(" + e.getMessage() + ")");
        }

        return redirect("/indexing");
    }

    public static Result interactive() {
        InteractiveRetrievalStub interactiveRetrievalStub = appLogic.loadInteractiveSearchSettings();
        Form<InteractiveRetrievalStub> interactiveRetrievalStubForm = Form.form(InteractiveRetrievalStub.class)
                .fill(interactiveRetrievalStub);

        return ok(interactive.render(INTERACTIVE_TITLE, interactiveRetrievalStubForm));
    }

    public static Result postInteractive() {
        Form<InteractiveRetrievalStub> interactiveRetrievalStubForm = Form.form(InteractiveRetrievalStub.class);
        InteractiveRetrievalStub interactiveSearchSettings = interactiveRetrievalStubForm.bindFromRequest().get();

        Calculator.TFType tfType = interactiveSearchSettings.getTf();
        boolean dIdf = interactiveSearchSettings.isUseIdf();
        boolean dNormalization = interactiveSearchSettings.isUseNormalization();
        boolean dStemmer = interactiveSearchSettings.isUseStemmer();

        try {
            appLogic.setSearchOption(new Option(tfType, dIdf, dNormalization, dStemmer));
            return redirect("/interactive");

        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError();
        }
    }

    /**
     * Search result page.
     *
     * @return
     */
    public static Result search() {
        Form<InteractiveRetrievalStub> experimentalRetrievalStubForm = Form.form(InteractiveRetrievalStub.class);
        InteractiveRetrievalStub interactiveRetrievalStub = experimentalRetrievalStubForm.bindFromRequest().get();

        String query = interactiveRetrievalStub.getQuery();
        List<Pair<Double, Integer>> similarityDocIDList = null;
        try {
            similarityDocIDList = appLogic.searchQuery(query);
            List<Pair<Double, IndexedDocument>> display = new ArrayList<>();
            for (int resIdx = 0; resIdx < similarityDocIDList.size(); resIdx++) {
                Pair<Double, Integer> similarityDocID = similarityDocIDList.get(resIdx);

                IndexedDocument document = appLogic.getIndexedDocument(similarityDocID.second);
                display.add(new Pair<>(similarityDocID.first, document));
            }

            return ok(interactivesearch.render("RESULT", display));

        } catch (IndexedFileException e) {
            flash("error", "The file has not been indexed yet ");
            // TODO return not_ok(), this return value below should not return ok because indexed file exception happened
            return internalServerError();
        }
    }

    public static Result experimental() {
        ExperimentalRetrievalStub experimentalRetrievalStub = appLogic.getExperimentSettings();
        Form<ExperimentalRetrievalStub> experimentalRetrievalStubForm = Form.form(ExperimentalRetrievalStub.class)
                .fill(experimentalRetrievalStub);
        return ok(experimental.render(EXPERIMENTAL_TITLE, experimentalRetrievalStubForm));
    }

    public static Result postExperimental() {
        Form<ExperimentalRetrievalStub> experimentalRetrievalStubForm = Form.form(ExperimentalRetrievalStub.class);
        ExperimentalRetrievalStub experimentalRetrievalStub = experimentalRetrievalStubForm.bindFromRequest().get();

        try {
            appLogic.setExperimentOption(new Option(
                    experimentalRetrievalStub.getTf(),
                    experimentalRetrievalStub.isUseIdf(),
                    experimentalRetrievalStub.isUseNormalization(),
                    experimentalRetrievalStub.isUseStemmer()
            ));
            flash("success", "Experiment Options have been saved");
            return redirect("/experimental");

        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError();
        }
    }

    public static Result performExperiment() {
        Form<ExperimentalRetrievalStub> experimentalRetrievalStubForm = Form.form(ExperimentalRetrievalStub.class);
        ExperimentalRetrievalStub experimentalRetrievalStub = experimentalRetrievalStubForm.bindFromRequest().get();

        try {
            appLogic.performExperiment(
                    experimentalRetrievalStub.getQueryLocation(),
                    experimentalRetrievalStub.getRelevantJudgementLocation()
            );
            return redirect("/experiment/result");

        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError();
        }
    }

    public static Result experimentResult() {
        return ok(experimentresult.render("Hasil Eksperimen", appLogic.getExperimentResults()));
    }

    public static Result experimentDetail(String s) {
        int idx = Integer.parseInt(s);
        return ok(experimentdetail.render("Detail Eksperimen", appLogic.getExperimentResult(idx)));
    }

    public static Result summary(){
        ExperimentalRetrievalStub experimentalRetrievalStub = appLogic.getExperimentSettings();
        Form<ExperimentalRetrievalStub> experimentalRetrievalStubForm = Form.form(ExperimentalRetrievalStub.class)
                .fill(experimentalRetrievalStub);
        return ok(summary.render("SUMMARY", experimentalRetrievalStubForm));
    }

    public static Result postSummary() throws IOException {
        Form<ExperimentalRetrievalStub> experimentalRetrievalStubForm = Form.form(ExperimentalRetrievalStub.class);
        ExperimentalRetrievalStub experimentalRetrievalStub = experimentalRetrievalStubForm.bindFromRequest().get();
        appLogic.writeSummary();
        return ok();
    }
}
