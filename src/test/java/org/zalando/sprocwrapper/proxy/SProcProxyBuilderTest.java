package org.zalando.sprocwrapper.proxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zalando.sprocwrapper.dsprovider.DataSourceProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SProcProxyBuilderTest {

    private interface EmptySProcService {
        // empty
    }

    @Mock
    private DataSourceProvider dataSourceProvider;

    private EmptySProcService underTest;

    @Before
    public void setUp() {
        underTest = SProcProxyBuilder.build(dataSourceProvider, EmptySProcService.class);
    }

    @Test
    public void hashCodeOnDynamicProxyWorks() {
        assertThat("hashCode() on dynamic proxy returned 0", underTest.hashCode(), is(not(0)));
    }

    @Test
    public void equalsOnDynamicProxyWorks() {
        assertThat("proxy.equals(proxy) is false", underTest.equals(underTest));
    }

    @Test
    public void toStringOnDynamicProxyWorks() {
        assertThat("toString() on dynamic proxy returned null", underTest.toString(), is(not(nullValue())));
    }
}
