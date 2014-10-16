package net.nemerosa.ontrack.extension.git;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Data
public class GitBranchesTemplateSynchronisationSourceConfig {

    private final String includes;
    private final String excludes;

    @JsonIgnore
    public Predicate<String> getFilter() {
        // Parsing
        Set<String> include = parse(includes);
        Set<String> exclude = parse(excludes);
        // Function
        return name -> isIncluded(include, name) && !isExcluded(exclude, name);
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

    protected static boolean isExcluded(Set<String> exclude, String name) {
        // FIXME Method net.nemerosa.ontrack.extension.git.GitBranchesTemplateSynchronisationSourceConfig.isExcluded
        return false;
    }

    protected static boolean isIncluded(Set<String> include, String name) {
        return false;
    }
}
