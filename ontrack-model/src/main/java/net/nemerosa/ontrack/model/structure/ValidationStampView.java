package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Validation stamp and its associated decorations
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidationStampView implements View {

    public static ValidationStampView of(ValidationStamp validationStamp, List<Decoration<?>> decorations) {
        return new ValidationStampView(validationStamp, decorations);
    }

    private final ValidationStamp validationStamp;
    private final List<Decoration<?>> decorations;
}
