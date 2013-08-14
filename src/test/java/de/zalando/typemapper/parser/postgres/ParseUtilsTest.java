package de.zalando.typemapper.parser.postgres;

import java.util.List;

import org.junit.Test;

import de.zalando.typemapper.parser.exception.ArrayParserException;
import de.zalando.typemapper.parser.exception.RowParserException;

import junit.framework.Assert;

/**
 * @author  hjacobs
 */
public class ParseUtilsTest {

    @Test(expected = ArrayParserException.class)
    public void testPostgresArray2StringListEmptyValue() throws ArrayParserException {
        ParseUtils.postgresArray2StringList("{a,}");
    }

    @Test(expected = ArrayParserException.class)
    public void testPostgresArray2StringListEmptyValue2() throws ArrayParserException {
        ParseUtils.postgresArray2StringList("{,,}");
    }

    @Test(expected = ArrayParserException.class)
    public void testPostgresArray2StringListEmptyValue3() throws ArrayParserException {
        ParseUtils.postgresArray2StringList("{a,,b}");
    }

    @Test
    public void testPostgresArray2StringListQuotedNull() throws ArrayParserException {
        List<String> fieldValues;
        fieldValues = ParseUtils.postgresArray2StringList("{a,NULL}");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertEquals("a", fieldValues.get(0));
        Assert.assertNull(fieldValues.get(1));

        fieldValues = ParseUtils.postgresArray2StringList("{a,\"NULL\"}");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertEquals("a", fieldValues.get(0));
        Assert.assertEquals("NULL", fieldValues.get(1));

    }

    @Test
    public void testPostgresArray2StringList() throws ArrayParserException {
        List<String> fieldValues;
        fieldValues = ParseUtils.postgresArray2StringList("{a,b}");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertEquals("a", fieldValues.get(0));
        Assert.assertEquals("b", fieldValues.get(1));

        fieldValues = ParseUtils.postgresArray2StringList("{a,NULL}");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertEquals("a", fieldValues.get(0));
        Assert.assertNull(fieldValues.get(1));

        fieldValues = ParseUtils.postgresArray2StringList("{a,\"\"}");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertEquals("a", fieldValues.get(0));
        Assert.assertEquals("", fieldValues.get(1));

        fieldValues = ParseUtils.postgresArray2StringList("{NULL,b}");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertNull(fieldValues.get(0));
        Assert.assertEquals("b", fieldValues.get(1));

        fieldValues = ParseUtils.postgresArray2StringList("{NULL,NULL}");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertNull(fieldValues.get(0));
        Assert.assertNull(fieldValues.get(1));

        fieldValues = ParseUtils.postgresArray2StringList("{a,NULL,NULL,b}");
        Assert.assertEquals(4, fieldValues.size());
        Assert.assertEquals("a", fieldValues.get(0));
        Assert.assertNull(fieldValues.get(1));
        Assert.assertNull(fieldValues.get(2));
        Assert.assertEquals("b", fieldValues.get(3));

    }

    @Test
    public void testPostgresRow2StringListSimple() throws RowParserException {

        List<String> fieldValues;
        fieldValues = ParseUtils.postgresROW2StringList("(a,b)");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertEquals("a", fieldValues.get(0));
        Assert.assertEquals("b", fieldValues.get(1));

        fieldValues = ParseUtils.postgresROW2StringList("(a,)");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertEquals("a", fieldValues.get(0));
        Assert.assertNull(fieldValues.get(1));

        fieldValues = ParseUtils.postgresROW2StringList("(,b)");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertNull(fieldValues.get(0));
        Assert.assertEquals("b", fieldValues.get(1));

        fieldValues = ParseUtils.postgresROW2StringList("(,)");
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertNull(fieldValues.get(0));
        Assert.assertNull(fieldValues.get(1));

        fieldValues = ParseUtils.postgresROW2StringList("(a,,,b)");
        Assert.assertEquals(4, fieldValues.size());
        Assert.assertEquals("a", fieldValues.get(0));
        Assert.assertNull(fieldValues.get(1));
        Assert.assertNull(fieldValues.get(2));
        Assert.assertEquals("b", fieldValues.get(3));
    }

    @Test
    public void testPostgresRow2StringList() throws RowParserException {

        List<String> fieldValues;

        fieldValues = ParseUtils.postgresROW2StringList("(,,0,1)");
        Assert.assertEquals(4, fieldValues.size());
        Assert.assertNull(fieldValues.get(0));
        Assert.assertNull(fieldValues.get(1));
        Assert.assertEquals("0", fieldValues.get(2));
        Assert.assertEquals("1", fieldValues.get(3));
    }

    @Test
    public void testPostgresRow2StringList2() throws RowParserException {

        List<String> fieldValues;

        fieldValues = ParseUtils.postgresROW2StringList("(,\"\",0,1)");
        Assert.assertEquals(4, fieldValues.size());
        Assert.assertNull(fieldValues.get(0));
        Assert.assertEquals("", fieldValues.get(1));
        Assert.assertEquals("0", fieldValues.get(2));
        Assert.assertEquals("1", fieldValues.get(3));

        fieldValues = ParseUtils.postgresROW2StringList("(,NULL,0,1)");
        Assert.assertEquals(4, fieldValues.size());
        Assert.assertNull(fieldValues.get(0));
        Assert.assertEquals("NULL", fieldValues.get(1));
        Assert.assertEquals("0", fieldValues.get(2));
        Assert.assertEquals("1", fieldValues.get(3));
    }

}
