package stbi.common.term;

import stbi.common.exception.NoTermException;

/**
 * Stemming Term Stream with Another TermStream as the source.
 * TODO by Winson, please implement this class.
 */
public class StemmingTermStream implements TermStream {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Term next() throws NoTermException {
        return null;
    }
}
