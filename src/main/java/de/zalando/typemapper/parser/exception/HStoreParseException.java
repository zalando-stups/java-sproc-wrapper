package de.zalando.typemapper.parser.exception;

import java.text.ParseException;

public class HStoreParseException extends ParseException {

    private static final long serialVersionUID = 1734348462350943810L;

    public HStoreParseException(final String s, final int errorOffset) {
        super(s, errorOffset);
    }

}
