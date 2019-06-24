package org.zalando.typemapper.namedresult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.typemapper.AbstractTest;
import org.zalando.typemapper.core.TypeMapper;
import org.zalando.typemapper.core.TypeMapperFactory;
import org.zalando.typemapper.namedresult.results.ClassWithColumnDefinition;
import org.zalando.typemapper.namedresult.results.ClassWithColumnDefinitionWithObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
public class ColumnTestIT extends AbstractTest {

    @Test
    public void testColumnMappings() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 1 as i, 2 as l, 'c' as c");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithColumnDefinition.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithColumnDefinition result = (ClassWithColumnDefinition) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getI());
            Assert.assertEquals(2, result.getL());
            Assert.assertEquals('c', result.getC());
        }
    }

    @Test
    public void testNestedColumnMappings() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement(
                "SELECT 'test' AS name, ROW(1, 2, 'c')::tmp.simple_type AS nested");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithColumnDefinitionWithObject.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithColumnDefinitionWithObject result = (ClassWithColumnDefinitionWithObject) mapper.mapRow(rs,
                    i++);
            Assert.assertNotNull(result);
            Assert.assertEquals("test", result.getName());

            final ClassWithColumnDefinition nested = result.getNested();
            Assert.assertNotNull(nested);

            Assert.assertEquals(1, nested.getI());
            Assert.assertEquals(2, nested.getL());
            Assert.assertEquals('c', nested.getC());
        }
    }

}
