package stbi.common;

import stbi.indexer.RawDocument;

/**
 * The indexed document type.
 * <p/>
 * Indexed document contains some information of the original document.
 */
public class IndexedDocument {
    private final int id;
    private final String title;
    private final String author;

    public IndexedDocument(RawDocument rawDocument) {
        id = rawDocument.getId();
        title = rawDocument.getTitle();
        author = rawDocument.getAuthor();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}
