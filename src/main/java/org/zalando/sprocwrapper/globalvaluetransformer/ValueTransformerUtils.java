package org.zalando.sprocwrapper.globalvaluetransformer;

import com.google.common.reflect.TypeToken;
import org.zalando.typemapper.core.ValueTransformer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;

public final class ValueTransformerUtils {
    private ValueTransformerUtils() { }

    public static Class<?> getMarshalToDbClass(final ValueTransformer<?, ?> valueTransformer) {
        return resolveReturnType(valueTransformer.getClass(), "marshalToDb", Object.class);
    }

    public static Class<?> getUnmarshalFromDbClass(final Class<?> clazz) {
        return resolveReturnType(checkValueTransformerClass(clazz), "unmarshalFromDb", String.class);
    }

    private static <T extends ValueTransformer<?, ?>> Class<?> resolveReturnType(final Class<T> actualClass,
            final String methodName, final Class<?>... methodParameterTypes) {
        final Method valueTransformerMethod = findValueTransformerMethod(methodName, methodParameterTypes);
        final Type genericReturnType = valueTransformerMethod.getGenericReturnType();
        return TypeToken.of(actualClass).resolveType(genericReturnType).getRawType();
    }

    private static Method findValueTransformerMethod(final String name, final Class<?>... parameterTypes) {
        try {
            return ValueTransformer.class.getMethod(name, parameterTypes);
        } catch (final NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("couldn't locate ValueTransformer method " + name, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends ValueTransformer<?, ?>> checkValueTransformerClass(final Class<?> clazz) {
        checkArgument(ValueTransformer.class.isAssignableFrom(clazz), "not a ValueTransformer: %s", clazz);
        return (Class<? extends ValueTransformer<?, ?>>) clazz;
    }
}
