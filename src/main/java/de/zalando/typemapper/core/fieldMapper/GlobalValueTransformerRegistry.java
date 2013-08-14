package de.zalando.typemapper.core.fieldMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.zalando.typemapper.core.ValueTransformer;

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
