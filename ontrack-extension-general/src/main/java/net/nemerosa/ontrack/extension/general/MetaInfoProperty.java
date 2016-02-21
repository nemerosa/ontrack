package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@Data
public class MetaInfoProperty {

    private final List<MetaInfoPropertyItem> items;

    /**
     * Does one of the items match the name-&gt;value? The value can be blank (meaning all values)
     * or contains wildcards (*).
     */
    public boolean matchNameValue(String name, String value) {
        return items.stream().anyMatch(item -> item.matchNameValue(name, value));
    }

    /**
     * Gets the property value for a given property name
     */
    public Optional<String> getValue(String name) {
        return items.stream()
                .filter(item -> StringUtils.equals(name, item.getName()))
                .map(MetaInfoPropertyItem::getValue)
                .findFirst();
    }
}
