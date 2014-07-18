package net.nemerosa.ontrack.extension.api;

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
    Optional<Object> getInformation(ProjectEntity entity);

}
