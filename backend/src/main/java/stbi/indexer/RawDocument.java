package stbi.indexer;

import java.io.Serializable;

/**
 * Raw Document Model.
 */
public class RawDocument implements Serializable {

    private final int id; // nomor dokumen
    private final String title; // judul dokumen
    private final String author; // nama pengarang dokumen
    private final String body; // isi dokumen

    public RawDocument(int id, String title, String author, String body) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RawDocument) {
            RawDocument mo = (RawDocument) o;
            return id == mo.id && title.equals(mo.title) && author.equals(mo.author) && body.equals(mo.body);
        }

        return false;
    }
}
