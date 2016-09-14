package net.nemerosa.ontrack.extension.general;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AutoValidationStampProperty {

    private final boolean autoCreate;
    private final boolean autoCreateIfNotPredefined;

    public AutoValidationStampProperty(boolean autoCreate) {
        this(autoCreate, false);
    }

}
