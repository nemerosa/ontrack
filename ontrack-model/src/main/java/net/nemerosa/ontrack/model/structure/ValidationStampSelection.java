package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import net.nemerosa.ontrack.model.support.Selectable;

import java.util.Objects;

@Data
public class ValidationStampSelection implements Selectable {

    private final ValidationStamp validationStamp;
    private final boolean selected;

    @Override
    public String getId() {
        return Objects.toString(validationStamp.id());
    }

    @Override
    public String getName() {
        return validationStamp.getName();
    }
}
