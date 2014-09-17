package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
public class Replacement {

    private final String regex;
    private final String replacement;

    public String replace(String value) {
        if (isNotBlank(regex) && isNotBlank(replacement)) {
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
}
