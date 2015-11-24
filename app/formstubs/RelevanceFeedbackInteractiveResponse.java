package formstubs;

import stbi.common.IndexedDocument;
import stbi.common.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nim_13512065 on 11/24/15.
 */
public class RelevanceFeedbackInteractiveResponse {
    private double similarity;
    private int documentId;
    private String title;
    private String author;

    public RelevanceFeedbackInteractiveResponse() {

    }

    public RelevanceFeedbackInteractiveResponse(Pair<Double, IndexedDocument> list) {
        this.similarity = list.first;
        this.documentId = list.second.getId();
        this.title = list.second.getTitle();
        this.author = list.second.getAuthor();
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> toArrayJsonValue() {
        List<String> tmp = new ArrayList<>();
        tmp.add(String.valueOf(getSimilarity()));
        tmp.add(String.valueOf(getDocumentId()));
        tmp.add(getTitle());
        return tmp;
    }
}
