package org.zalando.typemapper.sproctest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.typemapper.AbstractTest;
import org.zalando.typemapper.core.TypeMapper;
import org.zalando.typemapper.core.TypeMapperFactory;
import org.zalando.typemapper.namedresult.results.ClassWithStringSet;
import org.zalando.typemapper.namedresult.results.ClassWithStringSetWithEmbed;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
public class SprocArrayTestIT extends AbstractTest {

    @Test
    public void testStringSet() throws SQLException {
        connection.createStatement().execute("set search_path to tmp,tmp2");

        // DbFunctionRegister.reInitRegistry(connection);
        final PreparedStatement ps = connection.prepareStatement("SELECT tmp.string_array_function();");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<ClassWithStringSet> mapper = TypeMapperFactory.createTypeMapper(ClassWithStringSet.class);
        int i = 0;
        while (rs.next()) {
            ClassWithStringSet result = (ClassWithStringSet) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals("str", result.getStr());
            Assert.assertNotNull(result.getArray());
            Assert.assertTrue(result.getArray().size() == 2);
            Assert.assertTrue(result.getArray().contains("result_1"));
            Assert.assertTrue(result.getArray().contains("result_2"));
        }

    }

    @Test
    public void testNullStringSet() throws SQLException {
        connection.createStatement().execute("set search_path to tmp,tmp2");

        // DbFunctionRegister.reInitRegistry(connection);
        final PreparedStatement ps = connection.prepareStatement("SELECT tmp.string_null_array_function();");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<ClassWithStringSet> mapper = TypeMapperFactory.createTypeMapper(ClassWithStringSet.class);
        int i = 0;
        while (rs.next()) {
            ClassWithStringSet result = (ClassWithStringSet) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals("str", result.getStr());
            Assert.assertNotNull(result.getArray());
            Assert.assertTrue(result.getArray().isEmpty());
        }

    }

    @Test
    public void testNullStringSetWithEmbed() throws SQLException {
        connection.createStatement().execute("set search_path to tmp,tmp2");

        // DbFunctionRegister.reInitRegistry(connection);
        final PreparedStatement ps = connection.prepareStatement("SELECT tmp.string_null_array_function();");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<ClassWithStringSetWithEmbed> mapper = TypeMapperFactory.createTypeMapper(
                ClassWithStringSetWithEmbed.class);
        int i = 0;
        while (rs.next()) {
            ClassWithStringSetWithEmbed result = (ClassWithStringSetWithEmbed) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals("str", result.getSet().getStr());
            Assert.assertNotNull(result.getSet().getArray());
            Assert.assertTrue(result.getSet().getArray().isEmpty());
        }

    }

}
