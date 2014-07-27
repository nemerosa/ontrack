package net.nemerosa.ontrack.model.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageAnnotation {

    private final String type;
    private final String text;
    private final Map<String, String> attributes;

    public static MessageAnnotation of(String type) {
        return new MessageAnnotation(type, null, new HashMap<String, String>());
    }

    public static MessageAnnotation t(String text) {
        return of(null).text(text);
    }

    public MessageAnnotation attr(String name, String value) {
        attributes.put(name, value);
        return this;
    }

    public String attr(String name) {
        return attributes.get(name);
    }

    public MessageAnnotation text(String text) {
        return new MessageAnnotation(type, text, attributes);
    }

    public boolean isText() {
        return type == null;
    }

    public boolean hasText() {
        return StringUtils.isNotBlank(text);
    }
}
