package org.zalando.sprocwrapper.proxy;

import com.google.common.collect.Lists;
import org.zalando.sprocwrapper.globalvaluetransformer.ValueTransformerUtils;
import org.zalando.typemapper.core.ValueTransformer;
import org.zalando.typemapper.core.fieldMapper.ObjectMapper;
import org.zalando.typemapper.postgres.PgTypeHelper;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;

public class GlobalValueTransformedParameter extends StoredProcedureParameter {

    private final StoredProcedureParameter forwardingStoredProcedureParameter;

    private final ValueTransformer<?, ?> valueTransformerForClass;

    private final ObjectMapper<?> globalObjectMapper;

    public GlobalValueTransformedParameter(final ValueTransformer<?, ?> valueTransformerForClass, final Class<?> clazz,
            final Method m, final String typeName, final int javaPosition, final boolean sensitive,
            final ObjectMapper<?> globalObjectMapper) throws InstantiationException, IllegalAccessException {
        super(getValueTransformedClazz(clazz, valueTransformerForClass), m, typeName, getValueTransformedTypeId(clazz),
            javaPosition, sensitive);

        this.valueTransformerForClass = valueTransformerForClass;
        this.globalObjectMapper = globalObjectMapper;
        forwardingStoredProcedureParameter = StoredProcedureParameter.createParameter(getValueTransformedClazz(clazz,
                    valueTransformerForClass), getValueTransformedGenericClass(clazz), m,
                getValueTransformedTypeName(clazz, valueTransformerForClass), getValueTransformedTypeId(clazz),
                javaPosition, sensitive);
    }

    @Override
    public Object mapParam(final Object value, final Connection connection) {
        if (value == null) {
            return forwardingStoredProcedureParameter.mapParam(value, connection);
        }

        if (forwardingStoredProcedureParameter instanceof ArrayStoredProcedureParameter) {
            final List<Object> transformedValues = Lists.newArrayList();
            if (value.getClass().isArray()) {
                for (final Object o : ((Object[]) value)) {
                    transformedValues.add(getMarshaledObject(o));
                }
            } else {
                for (final Object o : ((Collection<?>) value)) {
                    transformedValues.add(String.valueOf(getMarshaledObject(o)));
                }
            }

            return forwardingStoredProcedureParameter.mapParam(transformedValues, connection);
        } else {
            return forwardingStoredProcedureParameter.mapParam(getMarshaledObject(value), connection);
        }
    }

    @SuppressWarnings("unchecked")
    private Object getMarshaledObject(final Object o) {
        if (globalObjectMapper != null) {
            return ((ObjectMapper<Object>) globalObjectMapper).marshalToDb(o);
        } else {
            return ((ValueTransformer<?, Object>) valueTransformerForClass).marshalToDb(o);
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
