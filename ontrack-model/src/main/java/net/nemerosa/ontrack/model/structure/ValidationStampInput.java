package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static net.nemerosa.ontrack.model.structure.NameDescription.NAME;
import static net.nemerosa.ontrack.model.structure.NameDescription.NAME_MESSAGE_SUFFIX;

@Data
public class ValidationStampInput {
    @NotNull(message = "The name is required.")
    @Pattern(regexp = ValidationStamp.NAME_REGEX, message = "The name " + NAME_MESSAGE_SUFFIX)
    private final String name;
    @Nullable
    private final String description;
    private final ServiceConfiguration dataType;

    public NameDescription asNameDescription() {
        return NameDescription.nd(name, description);
    }
}
