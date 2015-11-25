package controllers;

import formstubs.*;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import stbi.ApplicationLogic;
import stbi.IndexedFileException;
import stbi.RelevanceFeedbackDisplayVariables;
import stbi.common.IndexedDocument;
import stbi.common.Option;
import stbi.common.term.Term;
import stbi.common.util.Calculator;
import stbi.common.util.Pair;
import stbi.common.util.RelevanceFeedbackOption;
import stbi.common.util.RelevanceFeedbackStatus;
import views.html.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        RelevanceFeedbackStatus dRelevanceFeedbackStatus = interactiveSearchSettings.getRelevanceFeedbackStatus();
        RelevanceFeedbackOption dRelevanceFeedbackOption = interactiveSearchSettings.getRelevanceFeedbackOption();
        boolean dUseSameDocumentCollection = interactiveSearchSettings.isUseSameDocumentCollection();
        int dS = interactiveSearchSettings.getS();
        int dN = interactiveSearchSettings.getN();
        boolean dUseQueryExpansion = interactiveSearchSettings.isUseQueryExpansion();

        try {
            appLogic.setSearchOption(new Option(tfType, dIdf, dNormalization, dStemmer, dRelevanceFeedbackStatus, dRelevanceFeedbackOption, dUseSameDocumentCollection, dS, dN, dUseQueryExpansion));
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
        List<Pair<Double, Integer>> firstSearchResult = null;
        try {
            firstSearchResult = appLogic.searchQuery(query);

            List<Pair<Double, IndexedDocument>> displayableSearchResult = new ArrayList<>();
            for (int resIdx = 0; resIdx < firstSearchResult.size(); resIdx++) {
                Pair<Double, Integer> firstSearchEntry = firstSearchResult.get(resIdx);

                IndexedDocument document = appLogic.getIndexedDocument(firstSearchEntry.second);
                displayableSearchResult.add(new Pair<>(firstSearchEntry.first, document));
            }

            if (appLogic.getSearchOption().getRelevanceFeedbackStatus() == RelevanceFeedbackStatus.NO_RELEVANCE_FEEDBACK) {
                return ok(interactivesearchnorelevancefeedback.render("RESULT", displayableSearchResult));
            } else if (appLogic.getSearchOption().getRelevanceFeedbackStatus() == RelevanceFeedbackStatus.PSEUDO_RELEVANCE_FEEDBACK){
                RelevanceFeedbackDisplayVariables relevanceFeedbackDisplayVariables = appLogic.psuedoRelevanceFeedback(firstSearchResult);
                List<Pair<Double, IndexedDocument>> ret = new ArrayList<>();
                for (Pair<Double, Integer> it : relevanceFeedbackDisplayVariables.hasilPencarianBaru) {
                    IndexedDocument indexedDocument = appLogic.getIndexedDocument(it.second);
                    Pair<Double, IndexedDocument> tmp = new Pair<>(it.first, indexedDocument);
                    ret.add(tmp);
                }
                PseudoRelevanceFeedbackInteractiveResponse pseudoRelevanceFeedbackInteractiveResponse = new PseudoRelevanceFeedbackInteractiveResponse();
                pseudoRelevanceFeedbackInteractiveResponse.setHasilPencarian(ret);
                pseudoRelevanceFeedbackInteractiveResponse.setQueryBaru(relevanceFeedbackDisplayVariables.queryBaru);
                pseudoRelevanceFeedbackInteractiveResponse.setQueryLama(relevanceFeedbackDisplayVariables.queryLama);
                return ok(interactivesearchpseudorelevancefeedback.render("RESULT", pseudoRelevanceFeedbackInteractiveResponse));
            } else  {
                return ok(interactivesearch.render("RESULT", displayableSearchResult));
            }

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
                    experimentalRetrievalStub.isUseStemmer(),
                    experimentalRetrievalStub.getRelevanceFeedbackStatus(),
                    experimentalRetrievalStub.getRelevanceFeedbackOption(),
                    experimentalRetrievalStub.isUseSameDocumentCollection(),
                    experimentalRetrievalStub.getS(),
                    experimentalRetrievalStub.getN(),
                    experimentalRetrievalStub.isUseQueryExpansion()
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
        appLogic.writeRelevanceFeedbackSummary();
        return ok();
    }

    public static Result relFeedInteractive() {
        Form<RelevanceFeedbackInteractiveStub> relevanceFeedbackInteractiveStubForm = Form.form(RelevanceFeedbackInteractiveStub.class);
        RelevanceFeedbackInteractiveStub relevanceFeedbackInteractiveStub = relevanceFeedbackInteractiveStubForm.bindFromRequest().get();

        List<Integer> arg1 = relevanceFeedbackInteractiveStub.getRelevantList();
        List<Integer> arg2 = relevanceFeedbackInteractiveStub.getIrrelevantList();
        if (arg1 == null) {
            arg1 = new ArrayList<>();
        }

        if (arg2 == null){
            arg2 = new ArrayList<>();
        }
        RelevanceFeedbackDisplayVariables relevanceFeedbackDisplayVariables = appLogic.relevanceFeedback(arg1, arg2);

        //convert to json array
        Map<String, Object> retval = new HashMap<>();
        List<List<String>> datas = new ArrayList<>();
        for (Pair<Double, Integer> it : relevanceFeedbackDisplayVariables.hasilPencarianBaru) {
            IndexedDocument indexedDocument = appLogic.getIndexedDocument(it.second);
            Pair<Double, IndexedDocument> tmp = new Pair<>(it.first, indexedDocument);
            RelevanceFeedbackInteractiveResponse relevanceFeedbackInteractiveResponse = new RelevanceFeedbackInteractiveResponse(tmp);
            datas.add(relevanceFeedbackInteractiveResponse.toArrayJsonValue());
        }
        retval.put("data", datas);

        List<List<String>> qbaru = new ArrayList<>();
        if (relevanceFeedbackDisplayVariables.queryBaru != null) {
            for (Map.Entry entry : relevanceFeedbackDisplayVariables.queryBaru.entrySet()) {
                List<String> tmp = new ArrayList<>();
                Term t = (Term)entry.getKey();
                Double weight = (Double)entry.getValue();
                tmp.add(t.getText());
                tmp.add(String.valueOf(weight));
                qbaru.add(tmp);
            }
        }
        retval.put("qbaru", qbaru);

        List<List<String>> qlama = new ArrayList<>();
        if (relevanceFeedbackDisplayVariables.queryLama != null) {
            for (Map.Entry entry : relevanceFeedbackDisplayVariables.queryLama.entrySet()) {
                List<String> tmp = new ArrayList<>();
                Term t = (Term)entry.getKey();
                Double weight = (Double)entry.getValue();
                tmp.add(t.getText());
                tmp.add(String.valueOf(weight));
                qlama.add(tmp);
            }
        }
        retval.put("qlama", qlama);

        return ok(Json.toJson(retval));
    }
}
