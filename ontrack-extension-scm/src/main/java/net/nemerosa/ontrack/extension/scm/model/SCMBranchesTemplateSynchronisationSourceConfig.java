package net.nemerosa.ontrack.extension.scm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class SCMBranchesTemplateSynchronisationSourceConfig {

    private final String includes;
    private final String excludes;

    @JsonIgnore
    public Predicate<String> getFilter() {
        // Parsing
        Set<String> include = parse(includes);
        Set<String> exclude = parse(excludes);
        // Function
        return name -> matches(include, name, true) && !matches(exclude, name, false);
    }

    protected static Set<String> parse(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptySet();
        } else {
            return Arrays.asList(StringUtils.split(text, "\n\r")).stream()
                    .map(String::trim)
                    .filter(s -> s.length() != 0 && !s.startsWith("#"))
                    .collect(Collectors.toSet());
        }
    }

    protected static boolean matches(Set<String> patterns, String name, boolean emptyMeansAll) {
        if (patterns.isEmpty()) {
            return emptyMeansAll;
        } else {
            return patterns.stream()
                    .filter(pattern -> matches(pattern, name))
                    .findFirst()
                    .isPresent();
        }
    }

    private static boolean matches(String pattern, String name) {
        if ("*".equals(pattern)) {
            return true;
        } else if (pattern.contains("*")) {
            return Pattern.matches(
                    pattern.replaceAll("\\*", ".*"),
                    name
            );
        } else {
            return StringUtils.equals(pattern, name);
        }
    }
}
