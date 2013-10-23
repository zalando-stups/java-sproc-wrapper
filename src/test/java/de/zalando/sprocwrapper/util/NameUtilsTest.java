package de.zalando.sprocwrapper.util;

import org.junit.Assert;
import org.junit.Test;

public class NameUtilsTest {

    @Test
    public void testCamelCaseToUnderScore() {
        Assert.assertEquals("test_camel_case_to_under_score",
            NameUtils.camelCaseToUnderscore("testCamelCaseToUnderScore"));
    }

    @Test
    public void testCamelCaseToUnderScoreWithUpperWord() {
        Assert.assertEquals("test_camel_case_to_under_score",
            NameUtils.camelCaseToUnderscore("testCAMELCaseToUnderScore"));
    }

    @Test
    public void testUpperCamelCaseToUnderScore() {
        Assert.assertEquals("http_servlet", NameUtils.camelCaseToUnderscore("HTTPServlet"));
    }

    @Test
    public void testCamelCaseWithNumbersToUnderScore() {
        Assert.assertEquals("simple_size_2", NameUtils.camelCaseToUnderscore("simpleSize2"));
    }

}
