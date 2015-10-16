package controllers;

import formstubs.InteractiveRetrievalStub;
import formstubs.IndexingDocumentStub;
import formstubs.StopwordsStub;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import stbi.ApplicationLogic;
import stbi.IndexedFileException;
import stbi.common.Option;
import stbi.common.util.Calculator;
import stbi.common.util.Pair;
import views.html.interactive;
import views.html.search;
import views.html.stopwords;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {
    private static final String DOCUMENT_INDEXING_TITLE = "Document Indexing";
    private static final String EXPERIMENTAL_TITLE = "Experimental";
    private static final String INTERACTIVE_TITLE = "Experimental";
    private static final String STOPWORDS_TITLE = "Stopwords";


    public static Result index() {
        return redirect("/indexing");
    }

    public static Result indexingDocument() throws IOException {
        IndexingDocumentStub indexingDocumentStub = appLogic.getIndexingDocumentObjectFromJson();
        boolean idxFileExists = appLogic.indexFileExists();
        if (idxFileExists) {
            flash("info", "The index file exists");
        } else {
            flash("info", "The index file is not created yet");
        }
        Form<IndexingDocumentStub> indexingDocumentStubForm = Form.form(IndexingDocumentStub.class).fill(indexingDocumentStub);
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

    /**
     * TODO is this deprecated?
     * What is the purpose of this search? why does it get query from experimental retrieval stub?
     *
     * @return
     */
    public static Result search() {
        Form<InteractiveRetrievalStub> experimentalRetrievalStubForm = Form.form(InteractiveRetrievalStub.class);
        InteractiveRetrievalStub interactiveRetrievalStub = experimentalRetrievalStubForm.bindFromRequest().get();

        String query = interactiveRetrievalStub.getQuery();
        Calculator.TFType tfType = interactiveRetrievalStub.getqTf();
        boolean qIdf = interactiveRetrievalStub.isqIdf();
        boolean qNormalization = interactiveRetrievalStub.isqNormalization();
        boolean qStemmer = interactiveRetrievalStub.isqStemmer();
        Option searchOption = new Option(tfType, qIdf, qNormalization, qStemmer);
        appLogic.setSearchOptions(searchOption);

        List<Pair<Double, Integer>> similarityDocIDList = null;
        try {
            similarityDocIDList = appLogic.searchQuery(query);
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
            return ok(search.render("RESULT", similarityDocIDList));

        } catch (IndexedFileException e) {
            flash("error", "The file has not been indexed yet ");
            /*
            TODO return not_ok(), this return value below should not return ok
            because indexed file exception happened
            */
            return ok(search.render("RESULT", similarityDocIDList));
        }
    }

    public static final ApplicationLogic appLogic = ApplicationLogic.getInstance();

    public static Result experimental() {
        return play.mvc.Results.TODO;
    }

    public static Result interactive() {
        Form<InteractiveRetrievalStub> interactiveRetrievalStubForm = Form.form(InteractiveRetrievalStub.class);
        return ok(interactive.render(INTERACTIVE_TITLE, interactiveRetrievalStubForm));
    }

    public static Result stopwords() {
        StopwordsStub stopwordsStub = appLogic.getStopwordsDocumentObjectFromJson();
        Form<StopwordsStub> interactiveRetrievalStubForm = Form.form(StopwordsStub.class);
        if (stopwordsStub != null) {
            interactiveRetrievalStubForm = Form.form(StopwordsStub.class).fill(stopwordsStub);
        }
        return ok(stopwords.render(STOPWORDS_TITLE, interactiveRetrievalStubForm));
    }

    public static Result postStopwords() {
        Form<StopwordsStub> stopwordsStubForm = Form.form(StopwordsStub.class);
        StopwordsStub stopwordsStub = stopwordsStubForm.bindFromRequest().get();
        try {
            appLogic.saveStopwordsLocation(stopwordsStub);
            flash("success", "The stopwords has been saved");
            return redirect("/indexing");
        } catch (IOException e) {
            e.printStackTrace();
            flash("error", "The stopwords cannot be saved");
            return badRequest();
        }
    }
}
