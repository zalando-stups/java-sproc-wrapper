package org.zalando.typemapper.core.db;

public class DbTypeField {

    private final String name;
    private final int position;
    private final String type;
    private final String typeName;
    private final int typeId;

    public DbTypeField(final String fieldName, final int fieldPosition, final String fieldType,
            final String fieldTypeName, final int fieldTypeId) {
        this.name = fieldName;
        this.position = fieldPosition;
        this.type = fieldType;
        this.typeName = fieldTypeName;
        this.typeId = fieldTypeId;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    @Override
    public String toString() {
        return "DbTypeField [name=" + name + ", position=" + position + ", type=" + type + ", typeName=" + typeName
                + ", typeId=" + typeId + "]";
    }
}
