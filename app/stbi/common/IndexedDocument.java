package stbi.common;

import stbi.common.index.Index;
import stbi.indexer.RawDocument;

import java.io.Serializable;

/**
 * The indexed document type.
 * <p/>
 * Indexed document contains some information of the original document.
 */
public class IndexedDocument implements Serializable {
    private final int id;
    private final String title;
    private final String author;

    public IndexedDocument(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

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
