package de.zalando.typemapper.core;

import javax.persistence.Column;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.core.fieldMapper.AnyTransformer;

public class DatabaseFieldDescriptor {
    private String name;
    private int position;
    private Class<? extends ValueTransformer<?, ?>> transformer;

    public DatabaseFieldDescriptor(final DatabaseField databaseField) {
        this.name = databaseField.name();
        this.position = databaseField.position();
        this.transformer = databaseField.transformer();
    }

    public DatabaseFieldDescriptor(final Column column) {
        this.name = column.name();
        this.position = -1;
        this.transformer = AnyTransformer.class;
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
}
