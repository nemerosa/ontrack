package net.nemerosa.ontrack.model.settings;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;

import java.util.List;
import java.util.Optional;

/**
 * Management of predefined validation stamps.
 */
public interface PredefinedValidationStampService {

    /**
     * Gets the list of predefined validation stamps.
     */
    List<PredefinedValidationStamp> getPredefinedValidationStamps();

    /**
     * Creates a new predefined validation stamp.
     */
    PredefinedValidationStamp newPredefinedValidationStamp(PredefinedValidationStamp stamp);

    /**
     * Gets a predefined validation stamp using its ID
     */
    PredefinedValidationStamp getPredefinedValidationStamp(ID id);

    /**
     * Gets a predefined validation stamp using its name
     */
    Optional<PredefinedValidationStamp> findPredefinedValidationStampByName(String name);
}
