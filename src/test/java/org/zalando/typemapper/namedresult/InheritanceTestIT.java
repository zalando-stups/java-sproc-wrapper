package org.zalando.typemapper.namedresult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.typemapper.AbstractTest;
import org.zalando.typemapper.core.TypeMapper;
import org.zalando.typemapper.core.TypeMapperFactory;
import org.zalando.typemapper.namedresult.results.ChildClassWithPrimitives;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
public class InheritanceTestIT extends AbstractTest {

    @Test
    public void testPrimitiveMappings() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT 1 as i, 2 as l, 'c' as c");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ChildClassWithPrimitives.class);
        int i = 0;
        while (rs.next()) {
            final ChildClassWithPrimitives result = (ChildClassWithPrimitives) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getI());
            Assert.assertEquals(2, result.getL());
            Assert.assertEquals('c', result.getC());
        }
    }

}
