package org.zalando.typemapper.namedresult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.typemapper.AbstractTest;
import org.zalando.typemapper.core.TypeMapper;
import org.zalando.typemapper.core.TypeMapperFactory;
import org.zalando.typemapper.namedresult.results.ChildClassWithPrimitives;
import org.zalando.typemapper.namedresult.results.ClassWithListMembersInheritance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RunWith(SpringJUnit4ClassRunner.class)
public class CollectionWithMemberInhertinaceTestIT extends AbstractTest {

    @Test
    public void testListWithMemberIneritance() throws Exception {
        final PreparedStatement ps = connection.prepareStatement(
                "SELECT ARRAY[ROW(1,2,'c')::tmp.simple_type, ROW(1,2,'c')::tmp.simple_type]::tmp.simple_type[] as arr");
        final ResultSet rs = ps.executeQuery();
        final TypeMapper<ClassWithListMembersInheritance> mapper = TypeMapperFactory.createTypeMapper(
                ClassWithListMembersInheritance.class);
        int i = 0;
        while (rs.next()) {
            ClassWithListMembersInheritance result = (ClassWithListMembersInheritance) mapper.mapRow(rs, i++);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getArray());
            Assert.assertTrue(result.getArray().size() == 2);
            Assert.assertNotNull(result.getArray().get(0));
            Assert.assertNotNull(result.getArray().get(1));

            ChildClassWithPrimitives classWithPrimitives = result.getArray().get(0);
            Assert.assertEquals(1, classWithPrimitives.getI());
            Assert.assertEquals(2, classWithPrimitives.getL());
            Assert.assertEquals('c', classWithPrimitives.getC());
            classWithPrimitives = result.getArray().get(1);
            Assert.assertEquals(1, classWithPrimitives.getI());
            Assert.assertEquals(2, classWithPrimitives.getL());
            Assert.assertEquals('c', classWithPrimitives.getC());

        }
    }
}
