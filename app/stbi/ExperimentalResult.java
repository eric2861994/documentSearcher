package stbi;

/**
 * Created by nim_13512065 on 10/16/15.
 */
public class ExperimentalResult {
    private Double similarity;
    private Integer documentId;
    private String documentName;
    private Double precision;
    private Double recall;

    public ExperimentalResult() {

    }

    public ExperimentalResult(Double similarity, Integer documentId, String documentName, Double precision, Double recall) {
        setSimilarity(similarity);
        setDocumentId(documentId);
        setDocumentName(documentName);
        setPrecision(precision);
        setRecall(recall);
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    public Double getRecall() {
        return recall;
    }

    public void setRecall(Double recall) {
        this.recall = recall;
    }
}
