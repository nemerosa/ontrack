package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

public class JDKYearMonthSerializer extends JsonSerializer<YearMonth> {

    @Override
    public void serialize(YearMonth value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("year", value.getYear());
        map.put("month", value.getMonthValue());
        provider.defaultSerializeValue(
                map,
                jgen
        );
    }

}
