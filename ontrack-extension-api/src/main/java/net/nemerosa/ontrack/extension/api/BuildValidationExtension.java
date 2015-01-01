package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.extension.api.model.BuildValidationException;
import net.nemerosa.ontrack.model.structure.Build;

/**
 * Allows to validate the creation or update of builds in a branch.
 */
public interface BuildValidationExtension extends Extension {

    /**
     * Validates a build about to be saved or updated.
     */
    void validateBuild(Build build) throws BuildValidationException;
}
