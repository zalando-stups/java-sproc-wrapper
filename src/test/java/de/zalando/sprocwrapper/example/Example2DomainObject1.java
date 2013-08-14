package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;

public class Example2DomainObject1 {
    @DatabaseField(name = "example2_field1")
    private String example2Field1;

    @DatabaseField(name = "example2_field2")
    private Example2DomainObject2 example2Field2;

    public String getExample2Field1() {
        return example2Field1;
    }

    public void setExample2Field1(final String example2Field1) {
        this.example2Field1 = example2Field1;
    }

    public Example2DomainObject2 getExample2Field2() {
        return example2Field2;
    }

    public void setExample2Field2(final Example2DomainObject2 example2Field2) {
        this.example2Field2 = example2Field2;
    }

}
