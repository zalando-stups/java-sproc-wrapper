package org.zalando.sprocwrapper.example;

import java.util.List;

import org.zalando.typemapper.annotations.DatabaseField;
import org.zalando.typemapper.annotations.DatabaseType;

/**
 * @author mschumacher
 */
@DatabaseType
public class ExampleEnumDomainObject {

    @DatabaseField
    private Integer id;

    @DatabaseField
    private List<ExampleEnum> enumArray;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ExampleEnum> getEnumArray() {
        return enumArray;
    }

    public void setEnumArray(List<ExampleEnum> enumArray) {
        this.enumArray = enumArray;
    }
}