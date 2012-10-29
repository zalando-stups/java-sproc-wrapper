package de.zalando.sprocwrapper.proxy;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  henning
 */
public class SProcProxyBuilderTest {

    @Test
    public void testCamelCaseToUnderScore() {
        Assert.assertEquals("test_camel_case_to_under_score",
            SProcProxyBuilder.getNamingStrategy().getPGProcedureNameFromJavaName("testCamelCaseToUnderScore"));

        Assert.assertEquals("test_camel_case_to_under_score",
            SProcProxyBuilder.getNamingStrategy().getPGTypeNameFromJavaName("testCamelCaseToUnderScore"));
    }
}
