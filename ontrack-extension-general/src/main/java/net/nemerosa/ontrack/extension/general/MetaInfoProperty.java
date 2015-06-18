package net.nemerosa.ontrack.extension.general;

import lombok.Data;

import java.util.List;

@Data
public class MetaInfoProperty {

    private final List<MetaInfoPropertyItem> items;

    /**
     * Does one of the items match the name->value? The value can be blank (meaning all values)
     * or contains wildcards (*).
     */
    public boolean matchNameValue(String name, String value) {
        return items.stream().anyMatch(item -> item.matchNameValue(name, value));
    }
}
