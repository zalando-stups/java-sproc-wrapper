package org.zalando.typemapper.core.fieldMapper;

import org.zalando.typemapper.core.ValueTransformer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalValueTransformerRegistry {
    private static final Map<Class<?>, ValueTransformer<?, ?>> register =
        new ConcurrentHashMap<Class<?>, ValueTransformer<?, ?>>();

    public static void register(final Class<?> clazz, final ValueTransformer<?, ?> valueTransformer) {
        register.put(clazz, valueTransformer);
    }

    public static ValueTransformer<?, ?> getValueTransformerForClass(final Class<?> clazz) {
        return register.get(clazz);
    }
}
