package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static net.nemerosa.ontrack.model.structure.NameDescription.NAME;
import static net.nemerosa.ontrack.model.structure.NameDescription.NAME_MESSAGE_SUFFIX;

@Data
public class ValidationStampInput {
    @NotNull(message = "The name is required.")
    @Pattern(regexp = NAME, message = "The name " + NAME_MESSAGE_SUFFIX)
    private final String name;
    private final String description;
    private final ServiceConfiguration dataType;

    public NameDescription asNameDescription() {
        return NameDescription.nd(name, description);
    }
}
