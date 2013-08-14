package de.zalando.typemapper.core.fieldMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.zalando.typemapper.core.result.ArrayResultNode;
import de.zalando.typemapper.core.result.DbResultNode;
import de.zalando.typemapper.core.result.ObjectResultNode;
import de.zalando.typemapper.core.result.SimpleResultNode;
import de.zalando.typemapper.exception.NotsupportedTypeException;

public class ArrayFieldMapper {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object mapField(final Field field, final ArrayResultNode node) throws InstantiationException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException, NotsupportedTypeException {
        Collection result = null;
        if (field.getType().isAssignableFrom(List.class)) {
            result = new ArrayList();
        } else if (field.getType().isAssignableFrom(Set.class)) {
            result = new HashSet();
        }

        for (DbResultNode child : node.getChildren()) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            Type[] actualTypeArguments = type.getActualTypeArguments();
            Object obj = null;
            if (child instanceof ObjectResultNode) {
                obj = ObjectFieldMapper.mapField((Class) actualTypeArguments[0], (ObjectResultNode) child);
            } else if (child instanceof SimpleResultNode) {
                FieldMapper mapperForClass;
                if (actualTypeArguments[0] instanceof ParameterizedType) {
                    mapperForClass = FieldMapperRegister.getMapperForClass((Class)
                            ((ParameterizedType) actualTypeArguments[0]).getRawType());
                    obj = mapperForClass.mapField(child.getValue(),
                            (Class) ((ParameterizedType) actualTypeArguments[0]).getRawType());
                } else {
                    Class actualTypeClass = (Class) actualTypeArguments[0];
                    mapperForClass = FieldMapperRegister.getMapperForClass(actualTypeClass);
                    if (mapperForClass == null) {
                        throw new NotsupportedTypeException("Could not find mapper for type " + actualTypeClass);
                    }

                    obj = mapperForClass.mapField(child.getValue(), actualTypeClass);
                }
            }

            result.add(obj);
        }

        return result;
    }

}
