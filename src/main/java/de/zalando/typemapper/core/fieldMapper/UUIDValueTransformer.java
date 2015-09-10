package de.zalando.typemapper.core.fieldMapper;

import de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;
import de.zalando.typemapper.core.ValueTransformer;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by akushsky on 03.09.2015.
 */
@GlobalValueTransformer
public class UUIDValueTransformer extends ValueTransformer<PGobject, UUID> {

    @Override
    public UUID unmarshalFromDb(String value) {
        return UUID.fromString(value);
    }

    @Override
    public PGobject marshalToDb(UUID uuid) {
        PGobject pgUUID = new PGobject();
        pgUUID.setType("uuid");
        try {
            pgUUID.setValue(uuid.toString());
        } catch (SQLException e) {
            throw new ClassCastException(e.getMessage());
        }

        return pgUUID;
    }
}
