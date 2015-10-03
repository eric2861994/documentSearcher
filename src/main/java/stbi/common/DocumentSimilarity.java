package stbi.common;

/**
 * TODO comment.
 */
public class DocumentSimilarity implements Comparable<DocumentSimilarity> {
    private final double similarity;
    private final Document document;

    public DocumentSimilarity(double _similarity, Document _document) {
        similarity = _similarity;
        document = _document;
    }

    public double getSimilarity() {
        return similarity;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public int compareTo(DocumentSimilarity documentSimilarity) {
        double cmp = similarity - documentSimilarity.similarity;

        if (cmp < 0) {
            return -1;
        } else if (cmp > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
