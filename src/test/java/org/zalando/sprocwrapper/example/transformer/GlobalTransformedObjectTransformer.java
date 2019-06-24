package org.zalando.sprocwrapper.example.transformer;

import org.zalando.sprocwrapper.example.GlobalTransformedObject;
import org.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;
import org.zalando.typemapper.core.ValueTransformer;

@GlobalValueTransformer
public class GlobalTransformedObjectTransformer extends ValueTransformer<String, GlobalTransformedObject> {

    @Override
    public GlobalTransformedObject unmarshalFromDb(String value) {

        if (value == null || value.isEmpty()) {
            value = null;
        }

        return new GlobalTransformedObject(value);
    }

    @Override
    public String marshalToDb(final GlobalTransformedObject bound) {
        if (bound.getValue() == null) {
            return null;
        }

        return String.valueOf(bound.getValue());
    }
}
