package org.zalando.typemapper.namedresult.transformer;

import org.zalando.typemapper.core.ValueTransformer;
import org.zalando.typemapper.namedresult.results.GenderCode;

public class GenderCodeIntegerTransformer extends ValueTransformer<Integer, GenderCode> {

    @Override
    public GenderCode unmarshalFromDb(final String value) {
        return GenderCode.values()[Integer.valueOf(value)];
    }

    @Override
    public Integer marshalToDb(final GenderCode bound) {
        return bound.ordinal();
    }
}
