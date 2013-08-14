package de.zalando.typemapper.core;

public abstract class ValueTransformer<Value, Bound> {

    public abstract Bound unmarshalFromDb(String value);

    public abstract Value marshalToDb(Bound bound);
}
