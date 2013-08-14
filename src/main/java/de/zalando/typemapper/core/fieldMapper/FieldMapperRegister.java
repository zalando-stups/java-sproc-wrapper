package de.zalando.typemapper.core.fieldMapper;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.zalando.typemapper.core.ValueTransformer;

public class FieldMapperRegister {

    @SuppressWarnings({ "rawtypes" })
    private static final Map<Class, FieldMapper> register = new ConcurrentHashMap<Class, FieldMapper>();

    static {
        final FieldMapper dateFieldMapper = new DateFieldMapper();
        FieldMapperRegister.register(Date.class, dateFieldMapper);

        final FieldMapper integerMapper = new IntegerFieldMapper();
        FieldMapperRegister.register(Integer.class, integerMapper);

        final FieldMapper intMapper = new IntFieldMapper();
        FieldMapperRegister.register(int.class, intMapper);

        final FieldMapper longMapper = new LongFieldMapper();
        FieldMapperRegister.register(Long.class, longMapper);

        final FieldMapper primitiveLongMapper = new PrimitiveLongFieldMapper();
        FieldMapperRegister.register(long.class, primitiveLongMapper);

        final FieldMapper charMapper = new CharFieldMapper();
        FieldMapperRegister.register(char.class, charMapper);
        FieldMapperRegister.register(Character.class, charMapper);

        final FieldMapper stringMapper = new StringFieldMapper();
        FieldMapperRegister.register(String.class, stringMapper);

        final FieldMapper doubleMapper = new DoubleFieldMapper();
        FieldMapperRegister.register(Double.class, doubleMapper);
        FieldMapperRegister.register(double.class, doubleMapper);

        final FieldMapper floatMapper = new FloatFieldMapper();
        FieldMapperRegister.register(Float.class, floatMapper);
        FieldMapperRegister.register(float.class, floatMapper);

        final FieldMapper shortMapper = new ShortFieldMapper();
        FieldMapperRegister.register(Short.class, shortMapper);
        FieldMapperRegister.register(short.class, shortMapper);

        final FieldMapper booleanMapper = new BooleanFieldMapper();
        FieldMapperRegister.register(Boolean.class, booleanMapper);
        FieldMapperRegister.register(boolean.class, booleanMapper);

        final FieldMapper enumMapper = new EnumrationFieldMapper();
        FieldMapperRegister.register(Enum.class, enumMapper);

        final FieldMapper bigDecimalMapper = new BigDecimalFieldMappper();
        FieldMapperRegister.register(BigDecimal.class, bigDecimalMapper);

        final FieldMapper hstoreMapper = new HStoreFieldMapper();
        FieldMapperRegister.register(Map.class, hstoreMapper);

        final FieldMapper nullListMapper = new NullListFieldMapper();
        FieldMapperRegister.register(List.class, nullListMapper);

        final FieldMapper nullSetMapper = new NullSetFieldMapper();
        FieldMapperRegister.register(Set.class, nullSetMapper);
    }

    @SuppressWarnings("rawtypes")
    private static void register(final Class clazz, final FieldMapper mapper) {
        register.put(clazz, mapper);
    }

    @SuppressWarnings("rawtypes")
    public static FieldMapper getMapperForClass(final Class clazz) {
        FieldMapper fieldMapper = register.get(clazz);

        if (fieldMapper == null) {

            // check if there is a global field mapper defined in the value transformer register:
            final ValueTransformer<?, ?> valueTransformer = GlobalValueTransformerRegistry.getValueTransformerForClass(
                    clazz);
            if (valueTransformer != null) {
                fieldMapper = new ValueTransformerFieldMapper(valueTransformer);
                register.put(clazz, fieldMapper);
            }
        }

        if (fieldMapper == null) {
            if (clazz.getEnumConstants() != null) {
                fieldMapper = register.get(Enum.class);
            }
        }

        return fieldMapper;
    }

}
