package org.zalando.sprocwrapper.globalvaluetransformer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;


import org.zalando.typemapper.core.ValueTransformer;

public class ValueTransformerUtilsTest {

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

    @Test
    public void resolvesParamterizedUnmarshalReturnType() {
        assertThat(ValueTransformerUtils.getUnmarshalFromDbClass(ConcreteTransformer.class),
            is(sameInstance((Object) CustomBound.class)));
    }

    @Test
    public void resolvesParamterizedMarshalReturnType() {
        assertThat(ValueTransformerUtils.getMarshalToDbClass(new ConcreteTransformer()),
            is(sameInstance((Object) CustomValue.class)));
    }
}
