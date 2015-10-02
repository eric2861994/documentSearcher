package stbi.common.exception;

/**
 * Thrown in Term Stream when next is called, but no term exists.
 */
public class NoTermException extends Exception {
    public NoTermException() {
    }

    public NoTermException(String message) {
        super(message);
    }

    public NoTermException(Throwable cause) {
        super(cause);
    }

    public NoTermException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoTermException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
