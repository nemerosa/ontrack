package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
public class Replacement {

    private final String regex;
    private final String replacement;

    public String replace(String value) {
        if (value == null) {
            return null;
        } else if (isNotBlank(regex) && isNotBlank(replacement)) {
            Matcher m = Pattern.compile(regex).matcher(value);
            StringBuffer s = new StringBuffer();
            while (m.find()) {
                m.appendReplacement(s, replacement);
            }
            m.appendTail(s);
            return s.toString();
        } else {
            return value;
        }
    }

    public static Function<String, String> replacementFn(List<Replacement> replacements) {
        return (String value) -> {
            String transformedValue = value;
            for (Replacement replacement : replacements) {
                transformedValue = replacement.replace(transformedValue);
            }
            return transformedValue;
        };
    }

}
