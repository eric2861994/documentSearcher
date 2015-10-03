package stbi.common.term;

/**
 * Term representation.
 */
public class Term {
    private String text;

    Term(String _text) {
        text = _text;
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}
