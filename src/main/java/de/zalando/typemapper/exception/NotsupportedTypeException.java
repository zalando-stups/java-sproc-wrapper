package de.zalando.typemapper.exception;

public class NotsupportedTypeException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotsupportedTypeException() {
        super();
    }

    public NotsupportedTypeException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }

    public NotsupportedTypeException(final String arg0) {
        super(arg0);
    }

    public NotsupportedTypeException(final Throwable arg0) {
        super(arg0);
    }
}
