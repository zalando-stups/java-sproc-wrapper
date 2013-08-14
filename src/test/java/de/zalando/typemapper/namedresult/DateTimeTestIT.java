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
import de.zalando.typemapper.namedresult.results.ClassWithDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
public class DateTimeTestIT extends AbstractTest {

    @Test
    public void testDateTimeMappings() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT lt, gt, zone from tmp.test_time");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<?> mapper = TypeMapperFactory.createTypeMapper(ClassWithDateTime.class);
        int i = 0;
        while (rs.next()) {
            final ClassWithDateTime result = (ClassWithDateTime) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getDateWithTimezone(), result.getDateWithoutTimezone());
        }
    }
}
