package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidationRunStatus {

    private final Signature signature;
    private final ValidationRunStatusID statusID;
    private final String description;

    public static ValidationRunStatus of(Signature signature, ValidationRunStatusID validationRunStatusID, String description) {
        return new ValidationRunStatus(
                signature,
                validationRunStatusID,
                description
        );
    }
}
