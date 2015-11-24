package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.extension.api.model.EntityInformation;
import net.nemerosa.ontrack.model.extension.Extension;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.Optional;

/**
 * Defines an extension that can provide additional content to an
 * {@linkplain net.nemerosa.ontrack.model.structure.ProjectEntity entity}.
 */
public interface EntityInformationExtension extends Extension {

    /**
     * Gets information for an entity.
     */
    Optional<EntityInformation> getInformation(ProjectEntity entity);

}
