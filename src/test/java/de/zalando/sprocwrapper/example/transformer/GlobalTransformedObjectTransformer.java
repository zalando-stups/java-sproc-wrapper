package de.zalando.sprocwrapper.example.transformer;

import com.typemapper.core.ValueTransformer;

import de.zalando.sprocwrapper.example.GlobalTransformedObject;
import de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;

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
