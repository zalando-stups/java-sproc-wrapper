package de.zalando.typemapper.namedresult;

import java.math.BigDecimal;

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
import de.zalando.typemapper.namedresult.results.ClassWithBadPrimitives;
import de.zalando.typemapper.namedresult.results.ClassWithBigDecimal;
import de.zalando.typemapper.namedresult.results.ClassWithEmbed;
import de.zalando.typemapper.namedresult.results.ClassWithEmbedEnumClass;
import de.zalando.typemapper.namedresult.results.ClassWithPrimitives;
import de.zalando.typemapper.namedresult.results.Enumeration;

@RunWith(SpringJUnit4ClassRunner.class)
public class PrimitiveTestIT extends AbstractTest {

    @Test
    public void testPrimitiveMappings() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 1 as i, 2 as l, 'c' as c");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithPrimitives.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithPrimitives result = (ClassWithPrimitives) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getI());
            Assert.assertEquals(2, result.getL());
            Assert.assertEquals('c', result.getC());
        }
    }

    @Test
    public void testPrimitiveNotMappedFieldsMappings() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 1 as j, 2 as l, 'c' as c");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithPrimitives.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithPrimitives result = (ClassWithPrimitives) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals(0, result.getI());
            Assert.assertEquals(2, result.getL());
            Assert.assertEquals('c', result.getC());
        }
    }

    @Test
    public void testPrimitiveBadMappedMappings() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 1 as i, 2 as l, 'c' as c");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithBadPrimitives.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithBadPrimitives result = (ClassWithBadPrimitives) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals(0, result.getI());
            Assert.assertEquals(2, result.getL());
            Assert.assertEquals('c', result.getC());
        }
    }

    @Test
    public void testPrimitiveMappingsWithEmbed() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 1 as i, 2 as l, 'c' as c, 'str' as str");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithEmbed.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithEmbed result = (ClassWithEmbed) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result.getPrimitives());
            Assert.assertEquals(1, result.getPrimitives().getI());
            Assert.assertEquals(2, result.getPrimitives().getL());
            Assert.assertEquals('c', result.getPrimitives().getC());
            Assert.assertEquals("str", result.getStr());
        }
    }

    @Test
    public void testMappingsWithEmbedEnum() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 0 as a, 'VALUE_2' as b, 'str' as str");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithEmbedEnumClass.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithEmbedEnumClass result = (ClassWithEmbedEnumClass) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result.getEmbeddedEnum());
            Assert.assertEquals(Enumeration.VALUE_1, result.getEmbeddedEnum().getValue1());
            Assert.assertEquals(Enumeration.VALUE_2, result.getEmbeddedEnum().getValue2());
            Assert.assertEquals("str", result.getStr());
        }
    }

    @Test
    public void testMappingsWithEmbedEnumAndEnumDBTypes() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement(
                "SELECT 'VALUE_1'::enumeration as a, 'VALUE_2'::enumeration as b, 'str' as str");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithEmbedEnumClass.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithEmbedEnumClass result = (ClassWithEmbedEnumClass) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result.getEmbeddedEnum());
            Assert.assertEquals(Enumeration.VALUE_1, result.getEmbeddedEnum().getValue1());
            Assert.assertEquals(Enumeration.VALUE_2, result.getEmbeddedEnum().getValue2());
            Assert.assertEquals("str", result.getStr());
        }
    }

    @Test
    public void testPrimitiveBigDecimalMapper() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 1 as i");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithBigDecimal.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithBigDecimal result = (ClassWithBigDecimal) mapper.mapRow(rs, i++);
            Assert.assertEquals(new BigDecimal(1), result.getI());
        }
    }

    @Test
    public void testNullString() throws Exception {

        final PreparedStatement ps = connection.prepareStatement("SELECT null as str");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithEmbed.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithEmbed result = (ClassWithEmbed) mapper.mapRow(rs, i++);
            Assert.assertNull(result.getStr());
        }
    }

    @Test
    public void testEmptyString() throws Exception {

        final PreparedStatement ps = connection.prepareStatement("SELECT '' as str");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithEmbed.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithEmbed result = (ClassWithEmbed) mapper.mapRow(rs, i++);
            Assert.assertEquals("", result.getStr());
        }
    }

}
