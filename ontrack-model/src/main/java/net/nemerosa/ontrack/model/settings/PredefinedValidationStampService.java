package net.nemerosa.ontrack.model.settings;

import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;

import java.util.List;

/**
 * Management of predefined validation stamps.
 */
public interface PredefinedValidationStampService {

    /**
     * Gets the list of predefined validation stamps.
     */
    List<PredefinedValidationStamp> getPredefinedValidationStamps();

}
