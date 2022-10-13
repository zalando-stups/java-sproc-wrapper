package org.zalando.sprocwrapper.globalvaluetransformer;

import org.junit.Test;
import org.zalando.typemapper.core.ValueTransformer;

import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class ValueTransformerUtilsTest {

    @Test
    public void resolvesParametrizedUnmarshalReturnType() {
        assertThat(ValueTransformerUtils.getUnmarshalFromDbClass(ConcreteTransformer.class),
                is(sameInstance((Object) CustomBound.class)));
    }

    @Test
    public void resolvesParametrizedMarshalReturnType() {
        assertThat(ValueTransformerUtils.getMarshalToDbClass(new ConcreteTransformer()),
                is(sameInstance((Object) CustomValue.class)));
    }

    @Test
    public void parseNamespaces() {
        Set<String> namespaces = GlobalValueTransformerLoader.parseNamespaces("a.b.c;d;e ; f");
        assertThat(namespaces.size(), is(4));
        assertThat(namespaces, hasItems("a.b.c", "d", "e", "f"));
    }

    @Test
    public void parseEmptyNamespace() {
        Set<String> namespaces = GlobalValueTransformerLoader.parseNamespaces("");
        assertThat(namespaces.isEmpty(), is(true));
    }

    @Test
    public void parseBlankNamespace() {
        Set<String> namespaces = GlobalValueTransformerLoader.parseNamespaces(" ");
        assertThat(namespaces.isEmpty(), is(true));
    }

    /**
     * A custom value that shall be transformed using a {@link ValueTransformer}.
     */
    public static class CustomValue {
        // just a dummy type
    }

    /**
     * A custom bound that shall be transformed using a {@link ValueTransformer}.
     */
    public static class CustomBound {
        // just a dummy type
    }

    /**
     * An example for an abstract parameterized transformer that does &quot;something&quot; and then delegates to a
     * subclass's method.
     */
    public abstract static class AbstractParameterizedTransformer<Value, Bound> extends ValueTransformer<Value, Bound> {

        @Override
        public Bound unmarshalFromDb(final String value) {
            return delegatedUnmarshalling(value);
        }

        @Override
        public Value marshalToDb(final Bound bound) {
            return delegatedMarshalling(bound);
        }

        // actual unmarshalling performed by subclasses that operate on a preprocessed value
        protected abstract Bound delegatedUnmarshalling(String preprocessed);

        // actual marshalling performed by subclasses that operate on a preprocessed value
        protected abstract Value delegatedMarshalling(Bound preprocessed);
    }

    /**
     * An example for a concrete transformer that uses an abstract parameterized transformer as a superclass.
     */
    public static class ConcreteTransformer extends AbstractParameterizedTransformer<CustomValue, CustomBound> {
        @Override
        protected CustomBound delegatedUnmarshalling(final String preprocessed) {
            return null; // doesn't do anything meaningful, just an example
        }

        @Override
        protected CustomValue delegatedMarshalling(final CustomBound preprocessed) {
            return null; // doesn't do anything meaningful, just an example
        }
    }
}
