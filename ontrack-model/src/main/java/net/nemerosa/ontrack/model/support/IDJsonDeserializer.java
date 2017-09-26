package net.nemerosa.ontrack.model.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.nemerosa.ontrack.model.structure.ID;

import java.io.IOException;

public class IDJsonDeserializer extends JsonDeserializer<ID> {
    @Override
    public ID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Integer value = p.readValueAs(Integer.class);
        return value != null ? ID.of(value) : ID.NONE;
    }
}
