package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@Data
public class MetaInfoPropertyItem {

    private final String name;
    private final String value;
    private final String link;

    /**
     * Does one of the items match the name->value? The value can be blank (meaning all values)
     * or contains wildcards (*).
     */
    public boolean matchNameValue(String namePattern, String valuePattern) {
        return StringUtils.equals(this.name, namePattern) &&
                (
                        StringUtils.isBlank(valuePattern) ||
                                StringUtils.equals("*", valuePattern) ||
                                Pattern.matches(StringUtils.replace(valuePattern, "*", ".*"), this.value)
                );
    }
}
