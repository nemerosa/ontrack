package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public final class JsonUtils {

    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private JsonUtils() {
    }

    public static ObjectBuilder object() {
        return new ObjectBuilder(factory);
    }

    public static ArrayBuilder array() {
        return new ArrayBuilder(factory);
    }

    public static JsonNode stringArray(Iterable<String> values) {
        ArrayBuilder builder = array();
        for (String value : values) {
            builder.with(text(value));
        }
        return builder.end();
    }

    public static JsonNode stringArray(String... values) {
        return stringArray(Arrays.asList(values));
    }

    public static Map<String, ?> toMap(JsonNode node) throws IOException {
        if (node.isObject()) {
            Map<String, Object> map = new LinkedHashMap<>();
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldNode = node.get(fieldName);
                map.put(fieldName, toObject(fieldNode));
            }
            return map;
        } else {
            throw new IllegalArgumentException("Can only transform a JSON object into a map");
        }
    }

    public static Object toObject(JsonNode node) throws IOException {
        JsonNodeType type = node.getNodeType();
        switch (type) {
            case ARRAY:
                List<Object> list = new ArrayList<>();
                for (JsonNode child : node) {
                    list.add(toObject(child));
                }
                return list;
            case BINARY:
                return node.binaryValue();
            case BOOLEAN:
                return node.booleanValue();
            case NULL:
                return null;
            case NUMBER:
                return node.numberValue();
            case OBJECT:
            case POJO:
                return toMap(node);
            default:
                return node.textValue();
        }
    }

    public static JsonNode text(String text) {
        return factory.textNode(text);
    }

    public static JsonNode number(int value) {
        return factory.numberNode(value);
    }

    public static JsonNode mapToJson(Map<String, String> parameters) {
        ObjectBuilder builder = object();
        if (parameters != null) {
            parameters.forEach(builder::with);
        }
        return builder.end();
    }

    public static String get(JsonNode data, String field, String defaultValue) {
        if (data.has(field)) {
            return data.path(field).textValue();
        } else {
            return defaultValue;
        }
    }

    public static int getInt(JsonNode data, String field, int defaultValue) {
        if (data.has(field)) {
            return data.path(field).asInt();
        } else {
            return defaultValue;
        }
    }

    public static boolean getBoolean(JsonNode data, String field, boolean defaultValue) {
        if (data.has(field)) {
            return data.path(field).asBoolean();
        } else {
            return defaultValue;
        }
    }

    public static LocalDate getDate(JsonNode data, String field, LocalDate defaultValue) {
        if (data.has(field) && !data.get(field).isNull()) {
            return JDKLocalDateDeserializer.parse(data.path(field).asText());
        } else {
            return defaultValue;
        }
    }

    public static List<String> getStringList(JsonNode data, String field) {
        if (data.has(field)) {
            List<String> list = new ArrayList<>();
            data.get(field).forEach(node -> list.add(node.asText()));
            return list;
        } else {
            return null;
        }
    }
}
