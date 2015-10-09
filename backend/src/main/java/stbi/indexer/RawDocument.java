package stbi.indexer;

/**
 * Raw Document Model.
 */
public class RawDocument {

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
}
