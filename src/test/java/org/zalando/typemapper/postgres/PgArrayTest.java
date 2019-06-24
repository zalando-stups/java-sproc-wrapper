package org.zalando.typemapper.postgres;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PgArrayTest {

    @Test
    public void testEquals() {
        PgArray<Short> array1 = PgArray.ARRAY(Arrays.asList((short) 1, (short) 2, (short) 3));
        PgArray<Short> array2 = PgArray.ARRAY(Arrays.asList((short) 1, (short) 2, (short) 3));
        assertEquals(array1, array2);

        PgArray<Integer> array3 = PgArray.ARRAY(Arrays.asList(1, 2, 3));
        assertNotEquals(array1, array3);
    }

    @Test
    public void testHashCode() {
        PgArray<Short> array1 = PgArray.ARRAY(Arrays.asList((short) 1, (short) 2, (short) 3));
        PgArray<Short> array2 = PgArray.ARRAY(Arrays.asList((short) 1, (short) 2, (short) 3));
        assertEquals(array1.hashCode(), array2.hashCode());

        PgArray<Integer> array3 = PgArray.ARRAY(Arrays.asList(1, 2, 3));
        assertNotEquals(array1.hashCode(), array3.hashCode());
    }
}
