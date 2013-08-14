package de.zalando.typemapper.namedresult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.zalando.typemapper.AbstractTest;
import de.zalando.typemapper.core.TypeMapper;
import de.zalando.typemapper.core.TypeMapperFactory;
import de.zalando.typemapper.namedresult.results.ClassWithColumnDefinition;

@RunWith(SpringJUnit4ClassRunner.class)
public class ColumnTestIT extends AbstractTest {

    @Test
    public void testColumnMappings() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 2 as l, 'c' as c");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithColumnDefinition.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithColumnDefinition result = (ClassWithColumnDefinition) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals(2, result.getL());
            Assert.assertEquals('c', result.getC());
        }
    }
}
