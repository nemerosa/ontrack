package net.nemerosa.ontrack.extension.git.support;

import lombok.Data;

import java.util.function.Function;

@Data
public class TagPattern {

    private final String pattern;

    public TagPattern clone(Function<String, String> replacementFunction) {
        return new TagPattern(
                replacementFunction.apply(pattern)
        );
    }
}
