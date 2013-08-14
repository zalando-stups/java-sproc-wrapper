package de.zalando.typemapper.core.fieldMapper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class FloatFieldMapperTest {

    private FloatFieldMapper floatFieldMapper;

    @Before
    public void setUp() {
        floatFieldMapper = new FloatFieldMapper();
    }

    @Test
    public void testShoulMapNullStringToNull() {
        assertThat(floatFieldMapper.mapField(null, Float.class), nullValue());
    }

    @Test
    public void testShouldMapFloatStringRepresentationToFloat() {
        assertThat((Float) floatFieldMapper.mapField("1.23", Float.class), equalTo(1.23f));
    }

}
