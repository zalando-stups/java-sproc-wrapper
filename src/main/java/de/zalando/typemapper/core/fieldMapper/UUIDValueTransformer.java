package de.zalando.typemapper.core.fieldMapper;

import de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;
import de.zalando.typemapper.core.ValueTransformer;

import java.util.UUID;

/**
 * Created by akushsky on 03.09.2015.
 */
@GlobalValueTransformer
public class UUIDValueTransformer extends ValueTransformer<String, UUID> {

    @Override
    public UUID unmarshalFromDb(String value) {
        return UUID.fromString(value);
    }

    @Override
    public String marshalToDb(UUID uuid) {
        return uuid.toString();
    }
}
