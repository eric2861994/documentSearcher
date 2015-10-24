package stbi;

import stbi.common.IndexedDocument;

public class SearchResultEntry {
    private int rank;
    private double similarity;
    private IndexedDocument indexedDocument;

    public SearchResultEntry() {
    }

    public SearchResultEntry(int rank, double similarity, IndexedDocument indexedDocument) {
        setRank(rank);
        setSimilarity(similarity);
        this.indexedDocument = indexedDocument;
    }

    public final int getRank() {
        return rank;
    }

    public final void setRank(int rank) {
        this.rank = rank;
    }

    public final double getSimilarity() {
        return similarity;
    }

    public final void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public int getDocumentId() {
        return indexedDocument.getId();
    }

    public String getDocumentTitle() {
        return indexedDocument.getTitle();
    }
}
