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
import de.zalando.typemapper.namedresult.results.ClassWithObject;
import de.zalando.typemapper.namedresult.results.ClassWithObjectWithEmbed;
import de.zalando.typemapper.namedresult.results.ClassWithObjectWithObject;

@RunWith(SpringJUnit4ClassRunner.class)
public class ObjectTestIT extends AbstractTest {

    @Test
    public void testPrimitiveMappings() throws SQLException {
        TypeMapperFactory.initTypeAndFunctionCaches(connection, "default");

        final PreparedStatement ps = connection.prepareStatement(
                "SELECT 'str' as str, ROW(1,2,'c')::tmp.simple_type as obj");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithObject.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithObject result = (ClassWithObject) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result.getPrimitives());
            Assert.assertEquals(1, result.getPrimitives().getI());
            Assert.assertEquals(2, result.getPrimitives().getL());
            Assert.assertEquals('c', result.getPrimitives().getC());
            Assert.assertEquals("str", result.getStr());
        }
    }

    @Test
    public void testPrimitiveMappingsWithEmbeds() throws SQLException {

        // SELECT 1 as i, 2 as l, 'c' as c, 'str' as str
        final PreparedStatement ps = connection.prepareStatement(
                "SELECT 'str' as str, ROW(1,2,'c','str')::tmp.simple_type_for_embed as obj");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithObjectWithEmbed.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithObjectWithEmbed result = (ClassWithObjectWithEmbed) mapper.mapRow(rs, i++);
            Assert.assertEquals("str", result.getStr());
            Assert.assertNotNull(result.getClassWithEmbed());
            Assert.assertEquals("str", result.getClassWithEmbed().getStr());
            Assert.assertEquals(1, result.getClassWithEmbed().getPrimitives().getI());
            Assert.assertEquals(2, result.getClassWithEmbed().getPrimitives().getL());
            Assert.assertEquals('c', result.getClassWithEmbed().getPrimitives().getC());
        }
    }

    @Test
    public void testObjectInObject() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement(
                "SELECT 'str' as str, ROW(ROW(1,2,'c')::tmp.simple_type,'str')::tmp.complex_type as obj");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithObjectWithObject.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithObjectWithObject result = (ClassWithObjectWithObject) mapper.mapRow(rs, i++);
            Assert.assertEquals("str", result.getStr());
            Assert.assertNotNull(result.getWithObj());
            Assert.assertEquals("str", result.getWithObj().getStr());
            Assert.assertNotNull("str", result.getWithObj().getPrimitives());
            Assert.assertEquals('c', result.getWithObj().getPrimitives().getC());
            Assert.assertEquals(2, result.getWithObj().getPrimitives().getL());
            Assert.assertEquals(1, result.getWithObj().getPrimitives().getI());
        }

    }

    @Test
    public void testNullObjectInObject() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement(
                "SELECT 'str' as str, ROW(null,'str')::tmp.complex_type as obj");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithObjectWithObject.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithObjectWithObject result = (ClassWithObjectWithObject) mapper.mapRow(rs, i++);
            Assert.assertEquals("str", result.getStr());
            Assert.assertNotNull(result.getWithObj());
            Assert.assertEquals("str", result.getWithObj().getStr());
            Assert.assertNull(result.getWithObj().getPrimitives());
        }

    }

    @Test
    public void testObjectStringMappingWithNullString() throws SQLException {

        // SELECT 1 as i, 2 as l, 'c' as c, 'str' as str
        final PreparedStatement ps = connection.prepareStatement(
                "SELECT ROW(1,2,'c', null)::tmp.simple_type_for_embed as obj");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithObjectWithEmbed.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithObjectWithEmbed result = (ClassWithObjectWithEmbed) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result.getClassWithEmbed());
            Assert.assertNull(result.getClassWithEmbed().getStr());
        }
    }

    @Test
    public void testObjectStringMappingWithEmptyString() throws SQLException {

        // SELECT 1 as i, 2 as l, 'c' as c, 'str' as str
        final PreparedStatement ps = connection.prepareStatement(
                "SELECT  ROW(1,2,'c','')::tmp.simple_type_for_embed as obj");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithObjectWithEmbed.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithObjectWithEmbed result = (ClassWithObjectWithEmbed) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result.getClassWithEmbed());
            Assert.assertEquals("", result.getClassWithEmbed().getStr());
        }
    }

}
