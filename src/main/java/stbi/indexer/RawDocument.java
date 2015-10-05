package stbi.indexer;

/**
 * Raw Document Model.
 */
public class RawDocument {

    //Raw Document FORMAT;
    public static final char SECTION_TOKEN = '.';
    public static final char SECTION_ID = 'I';
    public static final char SECTION_TITLE = 'T';
    public static final char SECTION_AUTHOR = 'A';
    public static final char SECTION_CONTENT = 'W';
    public static final char SECTION_INDEX = 'X';

    private int id; // nomor dokumen
    private String title; // judul dokumen
    private String author; // nama pengarang dokumen
    private String body; // isi dokumen

    public RawDocument(int id, String title, String author, String body) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.body = body;
    }

    String getBody() {
        return body;
    }
}
