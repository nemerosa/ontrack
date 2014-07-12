package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class NameDescription {

    /**
     * Regular expression to validate a name.
     */
    public static final String NAME = "[A-Za-z0-9\\.-_]+";

    /**
     * Message associated with the regular expression
     */
    public static final String NAME_MESSAGE_SUFFIX = "can only have letter, digits, dot (.), dashes (-) or underscores (_).";

    @NotNull(message = "The name is required.")
    @Pattern(regexp = NAME, message = "The name " + NAME_MESSAGE_SUFFIX)
    private final String name;
    private final String description;

}
