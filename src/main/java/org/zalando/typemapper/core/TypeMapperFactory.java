package org.zalando.typemapper.core;

import java.sql.Connection;
import java.sql.SQLException;


import org.zalando.typemapper.core.db.DbFunctionRegister;
import org.zalando.typemapper.core.fieldMapper.GlobalValueTransformerRegistry;

public class TypeMapperFactory {

    private TypeMapperFactory() {
        // private constructor: Factory cannot be instantiated
    }

    public static <ITEM> TypeMapper<ITEM> createTypeMapper(final Class<ITEM> clazz) {
        return new TypeMapper<ITEM>(clazz);
    }

    public static void initTypeAndFunctionCaches(final Connection connection, final String name) throws SQLException {
        DbFunctionRegister.initRegistry(connection, name);
    }

    public static void registerGlobalValueTransformer(final Class<?> clazz,
            final ValueTransformer<?, ?> valueTransformer) {
        GlobalValueTransformerRegistry.register(clazz, valueTransformer);
    }
}
