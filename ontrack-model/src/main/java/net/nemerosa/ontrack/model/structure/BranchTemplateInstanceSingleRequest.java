package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Request to create a template instance for one name only.
 */
@Data
public class BranchTemplateInstanceSingleRequest {

    /**
     * Name of the instance to create
     */
    @NotNull(message = "The name is required.")
    @Pattern(regexp = NameDescription.NAME, message = "The name " + NameDescription.NAME_MESSAGE_SUFFIX)
    private final String name;

}
