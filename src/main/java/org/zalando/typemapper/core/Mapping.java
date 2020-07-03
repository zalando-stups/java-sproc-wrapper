package org.zalando.typemapper.core;

import static org.zalando.typemapper.postgres.PgTypeHelper.getDatabaseFieldDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Optional;

import org.zalando.sprocwrapper.util.NameUtils;

import org.zalando.typemapper.annotations.Embed;
import org.zalando.typemapper.core.fieldMapper.AnyTransformer;
import org.zalando.typemapper.core.fieldMapper.FieldMapper;
import org.zalando.typemapper.core.fieldMapper.FieldMapperRegister;
import org.zalando.typemapper.core.fieldMapper.ValueTransformerFieldMapper;
import org.zalando.typemapper.exception.NotsupportedTypeException;

public class Mapping {

    private final String name;
    private final Class<? extends ValueTransformer<?, ?>> valueTransformer;
    private final Field field;
    private final boolean embed;
    private final Field embedField;
    private FieldMapper fieldMapper;
    private Map<Field, Method> setter = new ConcurrentHashMap<>();

    private static final Map<Class, List<Mapping>> cache = new ConcurrentHashMap<>();

    public static List<Mapping> getMappingsForClass(@SuppressWarnings("rawtypes") final Class clazz) {
        List<Mapping> result = cache.get(clazz);
        if (result == null) {
            synchronized (Mapping.class) {

                result = cache.get(clazz);
                if (result == null) {
                    result = getMappingsForClass(clazz, false, null);

                    @SuppressWarnings("rawtypes")
                    Class parentClass = clazz.getSuperclass();
                    while (parentClass != null) {
                        result.addAll(Mapping.getMappingsForClass(parentClass));
                        parentClass = parentClass.getSuperclass();
                    }

                    cache.put(clazz, result);
                }
            }
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    static List<Mapping> getMappingsForClass(final Class clazz, final boolean embed, final Field embedField) {
        final List<Mapping> result = new ArrayList<Mapping>();
        final List<Field> fields = new LinkedList<Field>();
        for (final Field field : clazz.getDeclaredFields()) {
            fields.add(field);
        }

        Class parentClass = clazz.getSuperclass();
        while (parentClass != null) {
            for (final Field field : parentClass.getDeclaredFields()) {
                fields.add(field);
            }

            parentClass = parentClass.getSuperclass();
        }

        for (final Field field : fields) {
            final DatabaseFieldDescriptor annotation = getDatabaseFieldDescriptor(field);
            if (annotation != null) {
                final String databaseFieldName = getDatabaseFieldName(field, annotation.getName());
                result.add(new Mapping(field, databaseFieldName, embed, embedField, annotation.getTransformer()));
            }

            if (!embed) {
                final Embed embedAnnotation = field.getAnnotation(Embed.class);
                if (embedAnnotation != null) {
                    result.addAll(getMappingsForClass(field.getType(), true, field));
                }
            }
        }

        return result;
    }

    Mapping(final Field field, final String name, final boolean embed, final Field embedField,
            final Class<? extends ValueTransformer<?, ?>> valueTransformer) {
        this.name = name;
        this.field = field;
        this.embed = embed;
        this.embedField = embedField;
        this.valueTransformer = valueTransformer;
    }

    @SuppressWarnings("rawtypes")
    public Class getFieldClass() {
        if (isOptionalField()) {
            return (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        } else {
            return field.getType();
        }
    }

    public boolean isOptionalField() {
        return Optional.class.isAssignableFrom(field.getType());
    }

    public Class<? extends ValueTransformer<?, ?>> getValueTransformer() {
        return valueTransformer;
    }

    public Method getSetter(final Field field) {
        Method method = setter.get(field);

        if (method == null) {
            final String setterName = "set" + capitalize(field.getName());
            try {
                method = field.getDeclaringClass().getDeclaredMethod(setterName, field.getType());
                setter.put(field, method);
            } catch (final Exception e) {
                // ignore and return null
            }
        }

        return method;
    }

    public Method getSetter() {
        return getSetter(field);
    }

    public Method getGetter(final Field field) {
        final String getterName = "get" + capitalize(field.getName());
        try {
            return field.getDeclaringClass().getDeclaredMethod(getterName);
        } catch (final Exception e) {
            return null;
        }

    }

    public String getName() {
        return name;
    }

    private static String capitalize(final String name) {
        if ((name == null) || (name.length() == 0)) {
            return name;
        }

        if (Character.isUpperCase(name.charAt(0))) {
            return name;
        }

        final char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);

    }

    public static final String getDatabaseFieldName(final Field field, final String annotationName) {
        if ((annotationName != null) && !annotationName.isEmpty()) {
            return annotationName;
        }

        return NameUtils.camelCaseToUnderscore(field.getName());
    }

    public Field getField() {
        return field;
    }

    public FieldMapper getFieldMapper() throws NotsupportedTypeException, InstantiationException,
        IllegalAccessException {
        if (fieldMapper == null) {
            final DatabaseFieldDescriptor databaseFieldDescriptor = getDatabaseFieldDescriptor(field);
            if ((databaseFieldDescriptor != null) && (databaseFieldDescriptor.getTransformer() != null)) {
                if (!AnyTransformer.class.equals(getValueTransformer())) {
                    fieldMapper = new ValueTransformerFieldMapper(getValueTransformer());
                    return fieldMapper;
                }
            }

            fieldMapper = FieldMapperRegister.getMapperForClass(getFieldClass());
        }

        return fieldMapper;
    }

    public void map(final Object target, Object value) throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException, InstantiationException {
        if (isOptionalField()) {
            value = Optional.fromNullable(value);
        }

        if (embed) {
            Object embedValue = getEmbedFieldValue(target);
            if (embedValue == null) {
                embedValue = initEmbed(target);
            }

            final Method setter = getSetter();
            if (setter != null) {
                setter.invoke(embedValue, value);
            } else {
                getField().setAccessible(true);
                getField().set(embedValue, value);
            }
        } else {
            final Method setter = getSetter();
            if (setter != null) {
                setter.invoke(target, value);
            } else {
                getField().setAccessible(true);
                getField().set(target, value);
            }
        }
    }

    private Object initEmbed(final Object target) throws InstantiationException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {

        final Method setter = getSetter(embedField);
        final Object value = embedField.getType().newInstance();
        if (setter != null) {
            setter.invoke(target, value);
        } else {
            getField().set(target, value);
        }

        return value;

    }

    private Object getEmbedFieldValue(final Object target) throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {
        final Method setter = getGetter(embedField);
        Object result = null;
        if (setter != null) {
            result = setter.invoke(target);
        } else {
            result = embedField.get(target);
        }

        return result;
    }

    @Override
    public String toString() {
        return "Mapping [name=" + name + ", field=" + field + ", embed=" + embed + ", embedField=" + embedField + "]";
    }
}
