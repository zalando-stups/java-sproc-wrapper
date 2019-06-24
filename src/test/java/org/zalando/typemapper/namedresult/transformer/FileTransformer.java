package org.zalando.typemapper.namedresult.transformer;

import org.zalando.typemapper.core.ValueTransformer;

import java.io.File;

public class FileTransformer extends ValueTransformer<String, File> {

    @Override
    public File unmarshalFromDb(final String value) {
        return new File(value);
    }

    @Override
    public String marshalToDb(final File bound) {
        return bound.getPath();
    }
}
