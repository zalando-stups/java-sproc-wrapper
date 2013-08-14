package de.zalando.typemapper.postgres;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import static de.zalando.typemapper.postgres.PgArray.ARRAY;
import static de.zalando.typemapper.postgres.PgRow.ROW;

import java.sql.SQLException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PgSerializerTest {

    // Fields
    private Object objectToSerialize;
    private String expectedString;

    /*
     * Constructor. The JUnit test runner will instantiate this class once for
     * every element in the Collection returned by the method annotated with
     *
     * @Parameters.
     */
    public PgSerializerTest(final Object objectToSerialize, final String expectedString) {
        this.objectToSerialize = objectToSerialize;
        this.expectedString = expectedString;
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
                    {new Date(112, 11, 1, 6, 6, 6), "2012-12-01 06:06:06.000000 +01:00:00"},
                    {new Date(112, 9, 1, 6, 6, 6), "2012-10-01 06:06:06.000000 +02:00:00"},
                    {1, "1"},
                    {Integer.valueOf(69), "69"},
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
     *
     * @throws  SerializationError
     */
    @Test
    public void serializationTest() {
        assertThat(PgTypeHelper.toPgString(this.objectToSerialize), is(this.expectedString));
    }

}
