package controllers;

import formstubs.DocumentSearcherStub;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import stbi.ApplicationLogic;
import stbi.common.Option;
import stbi.common.util.Calculator;
import stbi.common.util.Pair;
import views.html.result;
import views.html.template;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Application extends Controller {

    public static Result index() {
        Form<DocumentSearcherStub> documentSearcherStubForm = Form.form(DocumentSearcherStub.class);
        DocumentSearcherStub documentSearcherStub = new DocumentSearcherStub();
        return ok(template.render(documentSearcherStubForm));
    }

    public static Result submit() {
        DynamicForm form = Form.form().bindFromRequest();
        System.out.println(form.get("documentLocation"));
        System.out.println(form.get("queryLocation"));
        System.out.println(form.get("relevantJudgement"));
        System.out.println(form.get("stopwordLocation"));
        System.out.println(form.get("dIdf"));
        System.out.println(form.get("dTf"));
        System.out.println(form.get("dNormalization"));
        System.out.println(form.get("qIdf"));
        System.out.println(form.get("qTf"));
        System.out.println(form.get("qNormalization"));
        return ok(result.render());
    }

    public static Result indexDocuments() {
        File documentsFile = Play.application().getFile("dataset/CISI/cisi.all");
        File stopwordsFile = Play.application().getFile("res/stopwords.txt");
        try {
            Option searchOption = new Option(Calculator.TFType.RAW_TF, false, false, false);
            appLogic.setSearchOptions(searchOption);
            appLogic.indexDocuments(documentsFile, stopwordsFile, Calculator.TFType.RAW_TF, false, false, false);
            return ok("index successful");

        } catch (IOException e) {
            e.printStackTrace();
            return ok("index failed");
        }
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
