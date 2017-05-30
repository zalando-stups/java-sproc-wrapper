package de.zalando.typemapper.postgres;

import java.sql.SQLException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.cthul.matchers.CthulMatchers.matchesPattern;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


import static de.zalando.typemapper.postgres.PgArray.ARRAY;
import static de.zalando.typemapper.postgres.PgRow.ROW;

@RunWith(Parameterized.class)
public class PgSerializerTest {

    // Fields
    private Object objectToSerialize;
    private String expectedString;
    private Pattern expectedPattern;

    /*
     * Constructor. The JUnit test runner will instantiate this class once for
     * every element in the Collection returned by the method annotated with
     *
     * @Parameters.
     */
    public PgSerializerTest(final Object objectToSerialize, final Object expected) {
        this.objectToSerialize = objectToSerialize;
        if (expected instanceof String) {
            this.expectedString = (String) expected;
            this.expectedPattern = null;
        } else if (expected instanceof Pattern) {
            this.expectedString = null;
            this.expectedPattern = (Pattern) expected;
        } else {
           throw new IllegalArgumentException(String.format(
                   "Expected either a String or a Pattern, got: %s (%s)",
                   expected, expected == null ? null : expected.getClass()));
        }
    }

    /*
     * Test data generator. This method is called the the JUnit parameterized
     * test runner and returns a Collection of Arrays. For each Array in the
     * Collection, each array element corresponds to a parameter in the
     * constructor.
     */
    @SuppressWarnings("deprecation")
    @Parameters
    public static Collection<Object[]> generateData() throws SQLException {
        return Arrays.asList(
                new Object[][] {
                    {new Date(112, 11, 1, 6, 6, 6), Pattern.compile("2012-12-01 06:06:06[+-]?\\d{2}")},
                    {new Date(112, 9, 1, 6, 6, 6), Pattern.compile("2012-10-01 06:06:06[+-]?\\d{2}")},
                    {Date.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse("2017-05-14T12:34:56.123456789+02:00", Instant::from)), Pattern.compile("2017-05-14 12:34:56.123000[+-]?\\d{2}")},
                    {1, "1"},
                    {69, "69"},
                    {true, "t"},
                    {new int[] {1, 2, 3, 4}, "{1,2,3,4}"},
                    {new Integer[] {null, 2, 3, 4}, "{NULL,2,3,4}"},
                    {ARRAY("a", "b").asJdbcArray("text"), "{a,b}"},
                    {ARRAY("a", "b(x)").asJdbcArray("text"), "{a,b(x)}"},
                    {ARRAY("a", "b{x}").asJdbcArray("text"), "{a,\"b{x}\"}"},
                    {
                        ARRAY("first element", "second \"quoted\" element").asJdbcArray("text"),
                        "{\"first element\",\"second \\\"quoted\\\" element\"}"
                    },
                    {ROW(1, 2), "(1,2)"},
                    {ROW("a", "b{x}"), "(a,b{x})"},
                    {ROW("a", "b(x)"), "(a,\"b(x)\")"},
                    {ROW(1, 2, ARRAY("a", "b")), "(1,2,\"{a,b}\")"},
                    {ROW("a", "b", new int[] {1, 2, 3, 4}), "(a,b,\"{1,2,3,4}\")"},
                    {ROW("a", null, ARRAY(ROW(1), ROW(2), null)), "(a,,\"{(1),(2),NULL}\")"},
                    {
                        ROW("a", null, ARRAY(ROW(1, 11), ROW(2, 22), null)),
                        "(a,,\"{\"\"(1,11)\"\",\"\"(2,22)\"\",NULL}\")"
                    },
                });
    }

    /**
     * Test how SerializationUtils.toPgString() method works.
     */
    @Test
    public void serializationTest() {
        String result = PgTypeHelper.toPgString(this.objectToSerialize);
        if (this.expectedString != null) {
            assertThat(result, is(this.expectedString));
        } else {
            assertThat(result, matchesPattern(this.expectedPattern));
        }
    }

}
