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
import de.zalando.typemapper.namedresult.results.ClassWithEmbedEnumClass;
import de.zalando.typemapper.namedresult.results.ClassWithEnum;
import de.zalando.typemapper.namedresult.results.Enumeration;

@RunWith(SpringJUnit4ClassRunner.class)
public class EnumTestIT extends AbstractTest {

    @Test
    public void testEnumMappings() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 0 as a, 'VALUE_2' as b");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithEnum.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithEnum result = (ClassWithEnum) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals(Enumeration.VALUE_1, result.getValue1());
            Assert.assertEquals(Enumeration.VALUE_2, result.getValue2());
        }
    }

    @Test
    public void testEnumMappingsWithEnumType() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 0 as a, 'VALUE_2'::enumeration as b;");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithEnum.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithEnum result = (ClassWithEnum) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals(Enumeration.VALUE_1, result.getValue1());
            Assert.assertEquals(Enumeration.VALUE_2, result.getValue2());
        }
    }

    @Test
    public void testEnumMappingsWithEnumInComplexType() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement(
                "select * from simple_with_enumeration_type_function();");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithEmbedEnumClass.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithEmbedEnumClass result = (ClassWithEmbedEnumClass) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals("str", result.getStr());
            Assert.assertEquals(Enumeration.VALUE_1, result.getEmbeddedEnum().getValue1());
            Assert.assertEquals(Enumeration.VALUE_2, result.getEmbeddedEnum().getValue2());
        }
    }
}
