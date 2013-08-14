package de.zalando.typemapper.postgres;

import java.sql.Connection;

import java.util.Collection;
import java.util.Iterator;

abstract class AbstractPgCollectionSerializer<E> extends AbstractPgSerializer {

    protected final Collection<E> collection;

    protected AbstractPgCollectionSerializer(final Collection<E> c) {
        this.collection = c;
    }

    @Override
    public final boolean isNull() {
        return collection == null;
    }

    protected abstract char getOpeningChar();

    protected abstract char getClosingChar();

    @Override
    protected final String getEmpty() {
        return new StringBuilder(2).append(getOpeningChar()).append(getClosingChar()).toString();
    }

    @Override
    protected boolean isEmpty() {
        return collection.isEmpty();
    }

    protected abstract void quoteChar(StringBuilder sb, char ch);

    @Override
    protected abstract void appendNull(StringBuilder sb);

    @Override
    public final StringBuilder quote(final StringBuilder sb, final CharSequence s) {
        if (sb == null) {
            throw new NullPointerException("Passed StringBuilder should be not null");
        }

        if (s == null) {
            throw new NullPointerException("Null values should be processed by the caller");
        }

        final int l = s.length();
        if (l == 0) {
            return sb.append("\"\"");
        }

        // find if there are quotes or commas in the string
        final char openingChar = getOpeningChar();
        final char closingChar = getClosingChar();
        boolean needsQuotation = false;
        int neededLength = 0;
        for (int i = 0; i < l; i++) {
            final char ch = s.charAt(i);
            neededLength++;
            if (ch == DOUBLE_QUOTE || ch == BACKSLASH) {
                needsQuotation = true;
                neededLength++;
            }

            if (Character.isWhitespace(ch) || ch == COMMA || ch == openingChar || ch == closingChar) {
                needsQuotation = true;
            }
        }

        if (needsQuotation) {
            neededLength += 2;
        }

        sb.ensureCapacity(sb.length() + neededLength);

        // start quotation
        if (needsQuotation) {
            sb.append(DOUBLE_QUOTE);
        }

        for (int i = 0; i < l; i++) {
            final char ch = s.charAt(i);
            if (ch == DOUBLE_QUOTE || ch == BACKSLASH) {
                quoteChar(sb, ch);
            } else {
                sb.append(ch);
            }
        }

        if (needsQuotation) {
            sb.append(DOUBLE_QUOTE);
        }

        return sb;
    }

    @Override
    public final String toPgString(final Connection connection) {

        final Iterator<E> iterator = collection.iterator();
        StringBuilder sb = new StringBuilder();

        sb.append(getOpeningChar());

        boolean hasNext = iterator.hasNext();
        while (hasNext) {
            E element = iterator.next();
            if (element == null) {
                appendNull(sb);
            } else {
                quote(sb, PgTypeHelper.toPgString(element, connection));
            }

            hasNext = iterator.hasNext();
            if (hasNext) {
                sb.append(',');
            } else {
                break;
            }
        }

        sb.append(getClosingChar());
        return sb.toString();
    }
}
