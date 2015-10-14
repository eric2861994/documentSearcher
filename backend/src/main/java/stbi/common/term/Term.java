package stbi.common.term;

import java.io.Serializable;

/**
 * Word representation.
 */
public class Term implements Serializable {
    private String text;

    public Term(String _text) {
        text = _text;
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Term) {
            Term mo = (Term) o;
            return text.equals(mo.text);
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return text;
    }
}
