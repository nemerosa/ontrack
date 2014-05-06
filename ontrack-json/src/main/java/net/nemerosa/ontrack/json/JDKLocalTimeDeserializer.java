package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class JDKLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String s = jp.readValueAs(String.class);
        if (StringUtils.isNotBlank(s)) {
            return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            return null;
        }
    }
}
