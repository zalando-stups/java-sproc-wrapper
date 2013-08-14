/**
 *
 */
package de.zalando.typemapper.parser.postgres;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.zalando.typemapper.parser.exception.ArrayParserException;
import de.zalando.typemapper.parser.exception.ParserException;
import de.zalando.typemapper.parser.exception.RowParserException;

/**
 * @author  valgog
 */
public class ParseUtils {

    public static final List<String> postgresArray2StringList(final String value) throws ArrayParserException {
        return postgresArray2StringList(value, 16);
    }

    public static final List<String> postgresArray2StringList(final String value, final int appendStringSize)
        throws ArrayParserException {
        if (!(value.startsWith("{") && value.endsWith("}"))) {
            throw new ArrayParserException(String.format(
                    "postgresArray2StringList() ARRAY must begin with '{' and ends with '}': %s", value));
        }

        if (value.length() == 2) {
            return Collections.emptyList();
        }

        // This is a simple copy-paste from the ROW processing code, and
        // strictly speaking is not quite correct for PostgreSQL ARRAYs
        final List<String> result = new ArrayList<String>();

        final char[] c = value.toCharArray();

        StringBuilder element = new StringBuilder(appendStringSize);

        // this processor will fail if value has spaces between ',' and '"' or
        // ')'
        int i = 1;
        while (c[i] != '}') {
            if (c[i] == ',') {
                final char nextChar = c[i + 1];
                if (nextChar == ',' || nextChar == '}') {

                    throw new ArrayParserException("Empty array value at position " + i + " should be quoted: "
                            + value);
                }

                i++;
            } else if (c[i] == '\"') {
                i++;

                boolean insideQuote = true;
                while (insideQuote) {
                    final char nextChar = c[i + 1];
                    if (c[i] == '\"') {
                        if (nextChar == ',' || nextChar == '}') {
                            result.add(element.toString());
                            element = new StringBuilder(appendStringSize);
                            insideQuote = false;
                        } else if (nextChar == '\"') {
                            i++;
                            element.append(c[i]);
                        } else {
                            throw new ArrayParserException("postgresArray2StringList() char after \" is not valid");
                        }
                    } else if (c[i] == '\\') {
                        if (nextChar == '\\' || nextChar == '\"') {
                            i++;
                            element.append(c[i]);
                        } else {
                            throw new ArrayParserException("postgresArray2StringList() char after \\ is not valid");
                        }
                    } else {
                        element.append(c[i]);
                    }

                    i++;
                }
            } else {
                while (!(c[i] == ',' || c[i] == '}')) {
                    element.append(c[i]);
                    i++;
                }

                if ("NULL".equals(element.toString().toUpperCase(Locale.ENGLISH))) {
                    result.add(null);
                } else {
                    result.add(element.toString());
                }

                element = new StringBuilder(appendStringSize);
            }
        }

        return result;
    }

    public static final List<String> postgresROW2StringList(final String value) throws RowParserException {
        return postgresROW2StringList(value, 16);
    }

    public static final List<String> postgresROW2StringList(String value, final int appendStringSize)
        throws RowParserException {
        if ((value.startsWith("(") && !value.endsWith(")")) || (!value.startsWith("(") && value.endsWith(")"))) {
            throw new RowParserException("postgresROW2StringList() ROW must begin with '(' and ends with ')': "
                    + value);
        }

        if (!(value.startsWith("(") || value.endsWith(")"))) {

            // in case both elements are missing, we add them for processing:
            // this will be the case for enum types:
            value = "(" + value + ")";
        }

        final List<String> result = new ArrayList<String>();

        final char[] c = value.toCharArray();

        StringBuilder element = new StringBuilder(appendStringSize);

        // this processor will fail if value has spaces between ',' and '"' or
        // ')'
        int i = 1;
        while (c[i] != ')') {
            if (c[i] == ',') {
                final char nextChar = c[i + 1];
                if (c[i - 1] == '(') {

                    // we have an empty first position, that is we have a NULL value
                    result.add(null);
                }

                if (nextChar == ',' || nextChar == ')') {
                    result.add(null);
                }

                i++;
            } else if (c[i] == '\"') {
                i++;

                boolean insideQuote = true;
                while (insideQuote) {
                    final char nextChar = c[i + 1];
                    if (c[i] == '\"') {
                        if (nextChar == ',' || nextChar == ')') {
                            result.add(element.toString());
                            element = new StringBuilder(appendStringSize);
                            insideQuote = false;
                        } else if (nextChar == '\"') {
                            i++;
                            element.append(c[i]);
                        } else {
                            throw new RowParserException("postgresROW2StringList() char after \" is not valid");
                        }
                    } else if (c[i] == '\\') {
                        if (nextChar == '\\' || nextChar == '\"') {
                            i++;
                            element.append(c[i]);
                        } else {
                            throw new RowParserException("postgresROW2StringList() char after \\ is not valid");
                        }
                    } else {
                        element.append(c[i]);
                    }

                    i++;
                }
            } else {
                while (!(c[i] == ',' || c[i] == ')')) {
                    element.append(c[i]);
                    i++;
                }

                // if the element was not quoted and was empty, it is supposed
                // to be NULL
                result.add(element.length() > 0 ? element.toString() : null);
                element = new StringBuilder(appendStringSize);
            }
        }

        return result;
    }

    private static final String[] trueList = {"t", "true", "1"};
    private static final String[] falseList = {"f", "false", "0"};

    public static Boolean getBoolean(final String s) throws ParserException {
        if (s == null) {
            return null;
        }

        final String b = s.trim().toLowerCase(Locale.US).intern();
        for (int i = 0; i < 3; i++) {
            if (b.equals(trueList[i])) {
                return Boolean.TRUE;
            }

            if (b.equals(falseList[i])) {
                return Boolean.FALSE;
            }
        }

        throw new ParserException("Cannot convert sting [" + s + "] to Boolean");
    }

}
