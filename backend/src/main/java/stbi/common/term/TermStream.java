package stbi.common.term;

import stbi.common.exception.NoTermException;

/**
 * Stream of Terms.
 */
public interface TermStream {
    /**
     * Determine Whether there is still any available term.
     *
     * @return true if there is still any available term
     */
    boolean hasNext();

    /**
     * Get the next Term, throws NoTermException if no more term is available.
     *
     * @return next Term
     */
    Term next() throws NoTermException;
}
