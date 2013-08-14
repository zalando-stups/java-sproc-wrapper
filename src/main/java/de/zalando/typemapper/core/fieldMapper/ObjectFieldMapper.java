package de.zalando.typemapper.core.fieldMapper;

import java.lang.reflect.InvocationTargetException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.typemapper.core.Mapping;
import de.zalando.typemapper.core.result.ArrayResultNode;
import de.zalando.typemapper.core.result.DbResultNode;
import de.zalando.typemapper.core.result.DbResultNodeType;
import de.zalando.typemapper.core.result.ObjectResultNode;
import de.zalando.typemapper.exception.NotsupportedTypeException;

public class ObjectFieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectFieldMapper.class);

    @SuppressWarnings("unchecked")
    public static final Object mapField(@SuppressWarnings("rawtypes") final Class clazz, final ObjectResultNode node)
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NotsupportedTypeException {
        if (node.getChildren() == null) {
            return null;
        }

        Object result = null;

        // instantiate enums by string value
        if (clazz.isEnum()) {
            final DbResultNode currentNode = node.getChildByName(node.getType());
            result = Enum.valueOf(clazz, currentNode.getValue());
        } else {
            result = clazz.newInstance();

            final List<Mapping> mappings = Mapping.getMappingsForClass(clazz);

            for (final Mapping mapping : mappings) {
                final DbResultNode currentNode = node.getChildByName(mapping.getName());
                if (currentNode == null) {
                    LOG.warn("Could not find value of mapping: {} in class: {}", mapping.getName(), clazz);
                    continue;
                }

                try {
                    if (DbResultNodeType.SIMPLE.equals(currentNode.getNodeType())) {
                        mapping.map(result,
                            mapping.getFieldMapper().mapField(currentNode.getValue(), mapping.getFieldClass()));
                    } else if (DbResultNodeType.OBJECT.equals(currentNode.getNodeType())) {

                        /**
                         * TODO: Lookup Value Transformer, map to intermediate class, then use transformer to map to target class
                         */
                        mapping.map(result, mapField(mapping.getFieldClass(), (ObjectResultNode) currentNode));
                    } else if (DbResultNodeType.ARRAY.equals(currentNode.getNodeType())) {
                        mapping.map(result,
                            ArrayFieldMapper.mapField(mapping.getField(), (ArrayResultNode) currentNode));
                    }
                } catch (final Exception e) {
                    LOG.error("Failed to map property {} of class {}",
                        new Object[] {mapping.getName(), clazz.getSimpleName(), e});
                }
            }
        }

        return result;
    }

}
