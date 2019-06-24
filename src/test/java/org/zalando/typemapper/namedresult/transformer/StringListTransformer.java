package org.zalando.typemapper.namedresult.transformer;

import org.springframework.util.StringUtils;
import org.zalando.typemapper.core.ValueTransformer;

import java.util.Arrays;
import java.util.List;

public class StringListTransformer extends ValueTransformer<String, List<String>> {

    @Override
    public List<String> unmarshalFromDb(final String value) {
        return Arrays.asList(value.split("#"));
    }

    @Override
    public String marshalToDb(final List<String> bound) {
        String ret = "";
        for (final String string : bound) {
            ret += string + "#";
        }

        return StringUtils.trimTrailingCharacter(ret, '#');
    }

}
