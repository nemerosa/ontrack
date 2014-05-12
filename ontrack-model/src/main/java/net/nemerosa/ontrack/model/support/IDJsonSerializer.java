package net.nemerosa.ontrack.model.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.nemerosa.ontrack.model.structure.ID;

import java.io.IOException;

public class IDJsonSerializer extends JsonSerializer<ID> {
    @Override
    public void serialize(ID value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (value != null) {
            jgen.writeNumber(value.getValue());
        } else {
            jgen.writeNull();
        }
    }
}
