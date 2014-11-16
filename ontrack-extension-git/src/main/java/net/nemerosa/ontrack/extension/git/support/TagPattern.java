package net.nemerosa.ontrack.extension.git.support;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class TagPattern {

    private final String pattern;

    public TagPattern clone(Function<String, String> replacementFunction) {
        return new TagPattern(
                replacementFunction.apply(pattern)
        );
    }

    public boolean isValidTagName(String name) {
        return StringUtils.isBlank(pattern) || createRegex().matcher(name).matches();
    }

    public Optional<String> getBuildNameFromTagName(String tagName) {
        if (StringUtils.isBlank(pattern)) {
            return Optional.of(tagName);
        } else {
            Matcher matcher = createRegex().matcher(tagName);
            if (matcher.matches()) {
                if (matcher.groupCount() > 0) {
                    return Optional.of(matcher.group(1));
                } else {
                    return Optional.of(matcher.group(0));
                }
            } else {
                return Optional.empty();
            }
        }
    }

    public Optional<String> getTagNameFromBuildName(String buildName) {
        if (StringUtils.isBlank(pattern)) {
            return Optional.of(buildName);
        } else {
            // Extraction of the build pattern, if any
            String buildPartRegex = "\\((.*\\*/*)\\)";
            Pattern buildPartPattern = Pattern.compile(buildPartRegex);
            Matcher buildPartMatcher = buildPartPattern.matcher(pattern);
            if (buildPartMatcher.find()) {
                String buildPart = buildPartMatcher.group(1);
                if (Pattern.matches(buildPart, buildName)) {
                    StringBuffer tag = new StringBuffer();
                    do {
                        buildPartMatcher.appendReplacement(tag, buildName);
                    } while (buildPartMatcher.find());
                    buildPartMatcher.appendTail(tag);
                    return Optional.of(tag.toString());
                } else {
                    return Optional.empty();
                }
            } else if (createRegex().matcher(buildName).matches()) {
                return Optional.of(buildName);
            } else {
                return Optional.empty();
            }
        }
    }

    private Pattern createRegex() {
        return Pattern.compile(StringUtils.replace(pattern, "*", ".*"));
    }
}
