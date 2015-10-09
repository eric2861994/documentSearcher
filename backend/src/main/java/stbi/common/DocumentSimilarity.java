package stbi.common;

/**
 * TODO comment.
 */
public class DocumentSimilarity implements Comparable<DocumentSimilarity> {
    private final double similarity;
    private final IndexedDocument indexedDocument;

    public DocumentSimilarity(double _similarity, IndexedDocument _document) {
        similarity = _similarity;
        indexedDocument = _document;
    }

    public double getSimilarity() {
        return similarity;
    }

    public IndexedDocument getIndexedDocument() {
        return indexedDocument;
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
