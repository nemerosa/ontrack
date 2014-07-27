package net.nemerosa.ontrack.model.support.tree.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Markup {

    private final String type;
    private final String text;
    private final Map<String, String> attributes;

    public static Markup text(String text) {
        return new Markup(null, text, null);
    }

    public static Markup of(String type) {
        return new Markup(type, null, new HashMap<String, String>());
    }

    public static Markup of(String type, Map<String, String> attributes) {
        return of(type).attrs(attributes);
    }

    public Markup attrs(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
        return this;
    }

    public Markup attr(String name, String value) {
        attributes.put(name, value);
        return this;
    }

    @JsonIgnore
    public boolean isOnlyText() {
        return type == null;
    }
}
