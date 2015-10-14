package controllers;

import formstubs.IndexingDocumentStub;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import stbi.ApplicationLogic;
import stbi.common.Option;
import stbi.common.util.Calculator;
import stbi.common.util.Pair;
import views.html.result;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Application extends Controller {
    private static final String WELCOME_STRING = "Welcome to Document Searcher";
    public static Result index() {
        return redirect("/indexing");
    }

    public static Result indexingDocument() {
        String message = flash("success");
        Form<IndexingDocumentStub> indexingDocumentStubForm = Form.form(IndexingDocumentStub.class);
        return ok(views.html.index.render(WELCOME_STRING, indexingDocumentStubForm));
    }

    public static Result submit() {
        DynamicForm form = Form.form().bindFromRequest();
        String documentLocation = form.get("documentLocation");
        String stopwordLocation = form.get("stopwordLocation");
        Calculator.TFType tfType = Calculator.TFType.valueOf(form.get("dTf"));
        boolean dIdf = Boolean.parseBoolean(form.get("dIdf"));
        boolean dNormalization = Boolean.parseBoolean(form.get("dNormalization"));
        boolean dStemmer = Boolean.parseBoolean(form.get("dStemmer"));

        File documentsFile = Play.application().getFile(documentLocation);
        File stopwordsFile = Play.application().getFile(stopwordLocation);
        Option searchOption = new Option(tfType, dIdf, dNormalization, dStemmer);
        appLogic.setSearchOptions(searchOption);
        try {
            appLogic.indexDocuments(documentsFile, stopwordsFile, tfType, dIdf, dNormalization, dStemmer);
            flash("success", "The file has been created");
        } catch (IOException e) {
            flash("error", "The file cannot be created");
        }

        return redirect("/indexing");
    }

    public static void indexDocuments() throws IOException {

    }

    public static Result search(String query) {
        List<Pair<Double, Integer>> similarityDocIDList = appLogic.searchQuery(query);

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

        return ok(stringBuilder.toString());
    }

    public static final ApplicationLogic appLogic = ApplicationLogic.getInstance();
}
