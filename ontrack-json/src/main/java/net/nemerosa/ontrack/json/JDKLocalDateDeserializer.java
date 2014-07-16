package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class JDKLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String s = jp.readValueAs(String.class);
        return parse(s);
    }

    protected LocalDate parse(String s) {
        if (StringUtils.isNotBlank(s)) {
            // Tries first with date only
            try {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ignored) {
                try {
                    // ... with a time zone
                    return LocalDateTime.ofInstant(Instant.parse(s), ZoneOffset.UTC).toLocalDate();
                } catch (DateTimeParseException ignored2) {
                    // ... with a date and time
                    return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate();
                }
            }
        } else {
            return null;
        }
    }
}
