package de.zalando.sprocwrapper.globalvaluetransformer;

import java.lang.reflect.Method;

import com.typemapper.core.ValueTransformer;

public final class ValueTransformerUtils {
    private ValueTransformerUtils() { }

    public static Class<?> getMarshalToDbClass(final Class<?> valueTransformer) {
        final Method method = ReflectionUtils.findMethod(valueTransformer, "marshalToDb");
        if (method != null) {
            return method.getReturnType();
        }

        return null;
    }

    public static Class<?> getUnmarshalFromDbClass(final Class<?> valueTransformerClass) {
        final Method method = ReflectionUtils.findMethod(valueTransformerClass, "unmarshalFromDb");
        if (method != null) {
            return method.getReturnType();
        }

        return null;
    }

    public static Class<?> getMarshalToDbClass(final ValueTransformer<?, ?> valueTransformerForClass) {
        return getMarshalToDbClass(valueTransformerForClass.getClass());
    }

}
