package stbi.common.term;

import stbi.common.exception.NoTermException;

import java.util.Set;

/**
 * Term Stream with stopwords filtering.
 */
public class StopwordTermStream implements TermStream {
    private final TermStream sourceStream;
    private final Set<Term> stopwords;

    private Term nextTerm;

    public StopwordTermStream(TermStream termStream, Set<Term> _stopwords) {
        sourceStream = termStream;
        stopwords = _stopwords;

        nextTerm = fastForward();
    }

    private Term fastForward() {
        while (hasNext()) {
            Term forward = next();
            if (!stopwords.contains(forward)) {
                return forward;
            }
        }

        return null;
    }

    @Override
    public boolean hasNext() {
        return nextTerm != null;
    }

    @Override
    public Term next() throws NoTermException {
        if (hasNext()) {
            Term result = nextTerm;
            nextTerm = fastForward();
            return result;

        } else {
            throw new NoTermException();
        }
    }
}
