package net.nemerosa.ontrack.extension.svn.support;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.beans.ConstructorProperties;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.replace;

/**
 * Pattern which defines a relationship between a build name and a revision.
 * <p>
 * The pattern is a string which <i>must</i> contain the <code>{revision}</code> token. The rest
 * of the string may contain <code>*</code> tokens, which act as placeholders.
 */
@Data
public class RevisionPattern {

    /**
     * Pattern definition.
     */
    private final String pattern;

    @ConstructorProperties({"pattern"})
    public RevisionPattern(String pattern) {
        if (!StringUtils.contains(pattern, "{revision}")) {
            throw new IllegalArgumentException("Revision pattern must contain the {revision} token: " + pattern);
        }
        this.pattern = pattern;
    }

    /**
     * Checks if the {@code buildName} complies with this pattern.
     */
    public boolean isValidBuildName(String buildName) {
        return extractRevision(buildName).isPresent();
    }

    /**
     * Extracts the revision from a build name.
     */
    public OptionalLong extractRevision(String buildName) {
        // Gets the regex for the pattern
        String regex = getRegex();
        // Matching
        Matcher matcher = Pattern.compile(regex).matcher(buildName);
        if (matcher.matches()) {
            String token = matcher.group(1);
            return OptionalLong.of(
                    Long.parseLong(token, 10)
            );
        } else {
            return OptionalLong.empty();
        }
    }

    private String getRegex() {
        return "^" + replace(
                replace(pattern, "{revision}", "(\\d+)"),
                "*",
                ".*"
        ) + "$";
    }
}
