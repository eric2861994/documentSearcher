package stbi.common.term;

import stbi.common.exception.NoTermException;

import java.util.Scanner;

/**
 * Term Stream from String.
 */
public class StringTermStream implements TermStream {
    private final Scanner scanner;

    public StringTermStream(String source) {
        scanner = new Scanner(source);
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNext();
    }

    @Override
    public Term next() throws NoTermException {
        if (hasNext())
            return new Term(scanner.next());
        else
            throw new NoTermException("No more term exist!");
    }
}
