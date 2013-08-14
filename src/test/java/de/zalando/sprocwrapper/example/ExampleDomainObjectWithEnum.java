package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;

public class ExampleDomainObjectWithEnum {
    @DatabaseField
    public String x;

    @DatabaseField
    public ExampleDomainObject y;

    @DatabaseField
    public ExampleEnum myEnum;

    public ExampleEnum getMyEnum() {
        return myEnum;
    }

    public void setMyEnum(final ExampleEnum myEnum) {
        this.myEnum = myEnum;
    }

    public String getX() {
        return x;
    }

    public void setX(final String x) {
        this.x = x;
    }

    public ExampleDomainObject getY() {
        return y;
    }

    public void setY(final ExampleDomainObject y) {
        this.y = y;
    }

}
