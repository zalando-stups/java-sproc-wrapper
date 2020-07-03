package org.zalando.typemapper.namedresult.transformer;

import org.zalando.typemapper.core.ValueTransformer;
import org.zalando.typemapper.namedresult.results.GenderCode;

public class GenderCodeTransformer extends ValueTransformer<String, GenderCode> {

    @Override
    public GenderCode unmarshalFromDb(final String value) {
        return GenderCode.fromCode(value);
    }

    @Override
    public String marshalToDb(final GenderCode bound) {
        return bound.getCode();
    }

}
