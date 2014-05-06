package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.YearMonth;

public class JDKYearMonthDeserializer extends JsonDeserializer<YearMonth> {
    @Override
    public YearMonth deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.readValueAsTree();
        int year = node.path("year").asInt();
        int month = node.path("month").asInt();
        return YearMonth.of(year, month);
    }
}
