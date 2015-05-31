package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ValidationStamp;

import java.util.List;

@Data
public class AutoPromotionProperty {

    /**
     * List of needed validation stamps
     */
    private final List<ValidationStamp> validationStamps;

    public boolean contains(ValidationStamp vs) {
        return validationStamps.stream().anyMatch(v -> (v.id() == vs.id()));
    }
}
