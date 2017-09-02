package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public final class JsonUtils {

    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private static final ObjectMapper mapper = ObjectMapperFactory.create();

    private JsonUtils() {
    }

    public static <V> V parse(JsonNode node, Class<V> type) {
        try {
            return mapper.treeToValue(node, type);
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e);
        }
    }

    public static JsonNode parseAsNode(String text) {
        try {
            return mapper.readTree(text);
        } catch (IOException e) {
            throw new JsonParseException(e);
        }
    }

    public static JsonNode format(Object value) {
        return mapper.valueToTree(value);
    }

    public static String toJSONString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e);
        }
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

    public static JsonNode intArray(int... values) {
        ArrayBuilder builder = array();
        for (int value : values) {
            builder.with(number(value));
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

    public static JsonNode fromMap(Map<String, ?> map) {
        return ObjectMapperFactory.create().valueToTree(map);
    }

    public static String get(JsonNode data, String field) {
        return get(data, field, true, null);
    }

    public static String get(JsonNode data, String field, String defaultValue) {
        return get(data, field, false, defaultValue);
    }

    public static String get(JsonNode data, String field, boolean required, String defaultValue) {
        if (data.has(field)) {
            return data.path(field).textValue();
        } else if (required) {
            throw new JsonMissingFieldException(field);
        } else {
            return defaultValue;
        }
    }

    public static int getInt(JsonNode data, String field) {
        return getInt(data, field, true, 0);
    }

    public static int getInt(JsonNode data, String field, int defaultValue) {
        return getInt(data, field, false, defaultValue);
    }

    public static int getInt(JsonNode data, String field, boolean required, int defaultValue) {
        if (data.has(field)) {
            return data.path(field).asInt();
        } else if (required) {
            throw new JsonMissingFieldException(field);
        } else {
            return defaultValue;
        }
    }

    public static boolean getBoolean(JsonNode data, String field) {
        return getBoolean(data, field, true, false);
    }

    public static boolean getBoolean(JsonNode data, String field, boolean defaultValue) {
        return getBoolean(data, field, false, defaultValue);
    }

    public static boolean getBoolean(JsonNode data, String field, boolean required, boolean defaultValue) {
        if (data.has(field)) {
            return data.path(field).asBoolean();
        } else if (required) {
            throw new JsonMissingFieldException(field);
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

    public static String pretty(JsonNode node) throws JsonProcessingException {
        return ObjectMapperFactory.create().writerWithDefaultPrettyPrinter().writeValueAsString(node);
    }
}
