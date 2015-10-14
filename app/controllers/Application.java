package controllers;

import formstubs.ExperimentalRetrievalStub;
import formstubs.IndexingDocumentStub;
import play.Play;
import scala.collection.JavaConverters;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import stbi.ApplicationLogic;
import stbi.IndexedFileException;
import stbi.common.Option;
import stbi.common.util.Calculator;
import stbi.common.util.Pair;
import views.html.experimental;
import views.html.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {
    private static final String DOCUMENT_INDEXING_TITLE = "Document Indexing";
    private static final String EXPERIMENTAL_TITLE = "Experimental";

    public static Result index() {
        return redirect("/indexing");
    }

    public static Result indexingDocument() {
        Form<IndexingDocumentStub> indexingDocumentStubForm = Form.form(IndexingDocumentStub.class);
        return ok(views.html.index.render(DOCUMENT_INDEXING_TITLE, indexingDocumentStubForm));
    }

    public static Result postIndexingDocument() {
        Form<IndexingDocumentStub> indexingDocumentStubForm = Form.form(IndexingDocumentStub.class);
        IndexingDocumentStub indexingDocumentStub = indexingDocumentStubForm.bindFromRequest().get();

        String documentLocation = indexingDocumentStub.getDocumentLocation();
        String stopwordLocation = indexingDocumentStub.getStopwordLocation();
        Calculator.TFType tfType = indexingDocumentStub.getdTf();
        boolean dIdf = indexingDocumentStub.isdIdf();
        boolean dNormalization = indexingDocumentStub.isdNormalization();
        boolean dStemmer = indexingDocumentStub.isdStemmer();

        File documentsFile = Play.application().getFile(documentLocation);
        File stopwordsFile = Play.application().getFile(stopwordLocation);

        try {
            appLogic.indexDocuments(documentsFile, stopwordsFile, tfType, dIdf, dNormalization, dStemmer);
            flash("success", "The file has been created");
        } catch (IOException e) {
            flash("error", "The file cannot be created " + "(" + e.getMessage() + ")");
        }

        return redirect("/indexing");
    }

    public static Result testResult() {
        List<Pair<Double, Integer>> ret = new ArrayList<>();
        ret.add(new Pair<>(10.2, 10));
        ret.add(new Pair<>(3.4, 23));
        ret.add(new Pair<>(1.2, 5));
        return ok(search.render("TESTING", ret));
    }

    public static Result search() {
        Form<ExperimentalRetrievalStub> experimentalRetrievalStubForm = Form.form(ExperimentalRetrievalStub.class);
        ExperimentalRetrievalStub experimentalRetrievalStub = experimentalRetrievalStubForm.bindFromRequest().get();

        String query = experimentalRetrievalStub.getQuery();
        Calculator.TFType tfType = experimentalRetrievalStub.getqTf();
        boolean qIdf = experimentalRetrievalStub.isqIdf();
        boolean qNormalization = experimentalRetrievalStub.isqNormalization();
        boolean qStemmer = experimentalRetrievalStub.isqStemmer();
        Option searchOption = new Option(tfType, qIdf, qNormalization, qStemmer);
        appLogic.setSearchOptions(searchOption);

        List<Pair<Double, Integer>> similarityDocIDList = null;
        try {
            similarityDocIDList = appLogic.searchQuery(query);
        } catch (IndexedFileException e) {
            flash("error", "The file has not been indexed yet ");
        } finally {
            StringBuilder stringBuilder = new StringBuilder();
            for (int resIdx = 0; resIdx < similarityDocIDList.size(); resIdx++) {
                Pair<Double, Integer> similarityDocID = similarityDocIDList.get(resIdx);

                String title = appLogic.getIndexedDocument(similarityDocID.second).getTitle();
                int maxChars = 80;
                if (title.length() > maxChars) {
                    title = title.substring(0, maxChars - 3) + "...";

                } else {
                    StringBuilder titleBuilder = new StringBuilder();
                    titleBuilder.append(title);
                    for (int i = title.length(); i < maxChars; i++) {
                        titleBuilder.append(' ');
                    }

                    title = titleBuilder.toString();
                }

                stringBuilder.append(resIdx);
                stringBuilder.append(". ");
                stringBuilder.append(title);
                stringBuilder.append("\tSimilarity = ");
                stringBuilder.append(similarityDocID.first);
                stringBuilder.append('\n');
            }
            return ok(search.render("RESULT",similarityDocIDList));
        }
    }

    public static final ApplicationLogic appLogic = ApplicationLogic.getInstance();

    public static Result experimental() {
        Form<ExperimentalRetrievalStub> experimentalRetrievalStubForm = Form.form(ExperimentalRetrievalStub.class);
        return ok(experimental.render(EXPERIMENTAL_TITLE, experimentalRetrievalStubForm));
    }

    public static Result interactive() {
        return play.mvc.Results.TODO;
    }
}
