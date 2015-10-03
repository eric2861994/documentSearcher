package stbi.common.term;

import stbi.common.exception.NoTermException;
import stbi.common.term.stemmer.Stemmer;

/**
 * Stemming Term Stream with Another TermStream as the source.
 * TODO by Winson, please implement this class.
 */
public class StemmingTermStream implements TermStream {
    private final TermStream _stream;
    private final Stemmer _stemmer;

    public StemmingTermStream(TermStream stream, Stemmer stemmer) {
        _stream = stream;
        _stemmer = stemmer;
    }

    @Override
    public boolean hasNext() {
        return _stream.hasNext();
    }

    @Override
    public Term next() throws NoTermException {
        if (hasNext()) {
            Term rawTerm = _stream.next();
            String stemmedWord = _stemmer.stem(rawTerm.getText());
            return new Term(stemmedWord);
        } else {
            throw new NoTermException("No more term exist!");
        }
    }
}
