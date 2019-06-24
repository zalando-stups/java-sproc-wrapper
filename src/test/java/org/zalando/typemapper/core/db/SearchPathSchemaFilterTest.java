package org.zalando.typemapper.core.db;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SearchPathSchemaFilterTest {

    @Test
    public void testFilter() {
        List<String> functionNames = new ArrayList<String>();
        functionNames.add("tmp2.test");
        functionNames.add("tmp.test");

        List<String> searchPath = new ArrayList<String>();
        searchPath.add("tmp2");
        searchPath.add("tmp");

        String result = SearchPathSchemaFilter.filter(functionNames, searchPath);
        Assert.assertEquals("tmp2.test", result);
    }

}
