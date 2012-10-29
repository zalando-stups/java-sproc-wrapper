package de.zalando.sprocwrapper.example.transformer;

import com.typemapper.core.ValueTransformer;

public class StringTransformer extends ValueTransformer<String, String> {
    @Override
    public String unmarshalFromDb(final String value) {
        return new StringBuffer(value).reverse().toString();
    }

    @Override
    public String marshalToDb(final String bound) {
        return new StringBuffer(bound).reverse().toString();
    }
}
