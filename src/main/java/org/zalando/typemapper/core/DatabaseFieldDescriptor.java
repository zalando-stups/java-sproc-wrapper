package org.zalando.typemapper.core;

import javax.persistence.Column;

import org.zalando.typemapper.annotations.DatabaseField;
import org.zalando.typemapper.core.fieldMapper.AnyTransformer;
import org.zalando.typemapper.core.fieldMapper.DefaultObjectMapper;
import org.zalando.typemapper.core.fieldMapper.ObjectMapper;

public class DatabaseFieldDescriptor {
    private String name;
    private int position;
    private Class<? extends ValueTransformer<?, ?>> transformer;
    private Class<? extends ObjectMapper<?>> mapper;

    public DatabaseFieldDescriptor(final DatabaseField databaseField) {
        this.name = databaseField.name();
        this.position = databaseField.position();
        this.transformer = databaseField.transformer();
        this.mapper = databaseField.mapper();
    }

    public DatabaseFieldDescriptor(final Column column) {
        this.name = column.name();
        this.position = -1;
        this.transformer = AnyTransformer.class;
        this.mapper = DefaultObjectMapper.class;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public Class<? extends ValueTransformer<?, ?>> getTransformer() {
        return transformer;
    }

    public Class<? extends ObjectMapper<?>> getMapper() {
        return mapper;
    }

}
