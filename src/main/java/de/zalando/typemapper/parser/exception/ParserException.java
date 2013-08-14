package de.zalando.typemapper.parser.exception;

public class ParserException extends Exception {

    private static final long serialVersionUID = -1877004443888510377L;

    public ParserException() {
        super();
    }

    public ParserException(final String message) {
        super(message);
    }

    public ParserException(final Throwable cause) {
        super(cause);
    }

    public ParserException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
