package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import lombok.experimental.Wither;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@Data
public class MetaInfoPropertyItem {

    public static MetaInfoPropertyItem of(String name, String value) {
        return new MetaInfoPropertyItem(name, value, null, null);
    }

    private final String name;
    private final String value;
    @Wither
    private final String link;
    @Wither
    private final String category;

    /**
     * Does one of the items match the name-&gt;value? The value can be blank (meaning all values)
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
