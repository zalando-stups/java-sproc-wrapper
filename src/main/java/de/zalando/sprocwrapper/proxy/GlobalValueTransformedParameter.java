package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.sql.Connection;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import de.zalando.sprocwrapper.globalvaluetransformer.ValueTransformerUtils;

import de.zalando.typemapper.core.ValueTransformer;
import de.zalando.typemapper.postgres.PgTypeHelper;

public class GlobalValueTransformedParameter extends StoredProcedureParameter {

    private StoredProcedureParameter forwardingStoredProcedureParameter;
    @SuppressWarnings("rawtypes")
    private ValueTransformer valueTransformerForClass;

    public GlobalValueTransformedParameter(final ValueTransformer<?, ?> valueTransformerForClass, final Class<?> clazz,
            final Type genericType, final Method m, final String typeName, final int sqlType, final int javaPosition,
            final boolean sensitive) throws InstantiationException, IllegalAccessException {
        super(getValueTransformedClazz(clazz, valueTransformerForClass), m, typeName, getValueTransformedTypeId(clazz),
            javaPosition, sensitive);

        this.valueTransformerForClass = valueTransformerForClass;
        forwardingStoredProcedureParameter = StoredProcedureParameter.createParameter(getValueTransformedClazz(clazz,
                    valueTransformerForClass), getValueTransformedGenericClass(clazz), m,
                getValueTransformedTypeName(clazz, valueTransformerForClass), getValueTransformedTypeId(clazz),
                javaPosition, sensitive);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object mapParam(final Object value, final Connection connection) {
        if (value == null) {
            return forwardingStoredProcedureParameter.mapParam(value, connection);
        }

        if (forwardingStoredProcedureParameter instanceof ArrayStoredProcedureParameter) {
            final List<Object> transformedValues = Lists.newArrayList();
            if (value.getClass().isArray()) {
                for (final Object o : ((Object[]) value)) {
                    transformedValues.add(valueTransformerForClass.marshalToDb(o));
                }
            } else {
                for (final Object o : ((Collection<?>) value)) {
                    transformedValues.add(String.valueOf(valueTransformerForClass.marshalToDb(o)));
                }
            }

            return forwardingStoredProcedureParameter.mapParam(transformedValues, connection);
        } else {
            return forwardingStoredProcedureParameter.mapParam(valueTransformerForClass.marshalToDb(value), connection);
        }
    }

    @Override
    public int getJavaPos() {
        return forwardingStoredProcedureParameter.getJavaPos();
    }

    @Override
    public boolean isSensitive() {
        return forwardingStoredProcedureParameter.isSensitive();
    }

    @Override
    public int getType() {
        return forwardingStoredProcedureParameter.getType();
    }

    @Override
    public String getTypeName() {
        return forwardingStoredProcedureParameter.getTypeName();
    }

    private static int getValueTransformedTypeId(final Class<?> clazz) {

        // start type detection for transformed class
        return -1;
    }

    private String getValueTransformedTypeName(final Class<?> clazz,
            final ValueTransformer<?, ?> valueTransformerForClass) {
        final Class<?> valueTransformedGenericClass = getValueTransformedGenericClass(
                valueTransformerForClass.getClass());
        String sqlNameForClass = PgTypeHelper.getSQLNameForClass(valueTransformedGenericClass);

        if (sqlNameForClass != null && clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            sqlNameForClass += "[]";
        }

        return sqlNameForClass;
    }

    private static Class<?> getValueTransformedClazz(final Class<?> clazz,
            final ValueTransformer<?, ?> valueTransformerForClass) {
        if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            return List.class;
        }

        return ValueTransformerUtils.getMarshalToDbClass(valueTransformerForClass);
    }

    private Class<?> getValueTransformedGenericClass(final Class<?> clazz) {
        return ValueTransformerUtils.getMarshalToDbClass(valueTransformerForClass);
    }
}
