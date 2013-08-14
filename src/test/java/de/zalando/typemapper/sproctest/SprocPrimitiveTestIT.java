package de.zalando.typemapper.sproctest;

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
import de.zalando.typemapper.core.db.DbFunctionRegister;
import de.zalando.typemapper.sproctest.result.ArrayResult;
import de.zalando.typemapper.sproctest.result.PrimitiveResult;
import de.zalando.typemapper.sproctest.result.PrimitiveResult2;

@RunWith(SpringJUnit4ClassRunner.class)
public class SprocPrimitiveTestIT extends AbstractTest {

    @Test
    public void testPrimitives() throws SQLException {
        connection.createStatement().execute("set search_path to tmp,tmp2");

        final PreparedStatement ps = connection.prepareStatement("SELECT tmp.primitives_function();");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(PrimitiveResult.class);
        int i = 0;
        while (rs.next()) {
            final PrimitiveResult result = (PrimitiveResult) mapper.mapRow(rs, i++);
            Assert.assertEquals(0, result.getId().intValue());
            Assert.assertEquals("result_code", result.getMsg());
        }
    }

    @Test
    public void testPrimitivesWithNullString() throws SQLException {
        connection.createStatement().execute("set search_path to tmp,tmp2");

        final PreparedStatement ps = connection.prepareStatement("SELECT tmp.primitives_with_null_function();");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(PrimitiveResult.class);
        int i = 0;
        while (rs.next()) {
            final PrimitiveResult result = (PrimitiveResult) mapper.mapRow(rs, i++);
            Assert.assertEquals(0, result.getId().intValue());
            Assert.assertNull(result.getMsg());
        }
    }

    @Test
    public void testPrimitivesWithSearchPath() throws SQLException {
        connection.createStatement().execute("set search_path to tmp2,tmp");
        DbFunctionRegister.reInitRegistry(connection);

        final PreparedStatement ps = connection.prepareStatement("SELECT primitives_function();");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(PrimitiveResult2.class);
        int i = 0;
        while (rs.next()) {
            final PrimitiveResult2 result = (PrimitiveResult2) mapper.mapRow(rs, i++);
            Assert.assertEquals(0, result.getId().intValue());
            Assert.assertEquals("result_code", result.getMsg());
            Assert.assertEquals("result_code_2", result.getMsg2());
        }
    }

    @Test
    public void testObjectArray() throws SQLException {
        connection.createStatement().execute("set search_path to tmp,tmp2");

        final PreparedStatement ps = connection.prepareStatement("SELECT tmp.array_function();");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ArrayResult.class);
        int i = 0;
        while (rs.next()) {
            final ArrayResult result = (ArrayResult) mapper.mapRow(rs, i++);
            Assert.assertEquals(0, result.getId().intValue());
            Assert.assertEquals("result_code", result.getMsg());
            Assert.assertNotNull(result.getMovies());
            Assert.assertEquals(3, result.getMovies().size());
            Assert.assertEquals(1, result.getMovies().get(0).getI());
            Assert.assertEquals(2, result.getMovies().get(0).getL());
            Assert.assertEquals("Daniel", result.getMovies().get(0).getC());
            Assert.assertEquals(2, result.getMovies().get(1).getI());
            Assert.assertEquals(3, result.getMovies().get(1).getL());
            Assert.assertEquals("alone at", result.getMovies().get(1).getC());
            Assert.assertEquals(3, result.getMovies().get(2).getI());
            Assert.assertEquals(4, result.getMovies().get(2).getL());
            Assert.assertEquals("home", result.getMovies().get(2).getC());

        }
    }
}
