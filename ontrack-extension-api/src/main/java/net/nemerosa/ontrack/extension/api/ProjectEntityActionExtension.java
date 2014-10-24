package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.Optional;

/**
 * Allows a {@link net.nemerosa.ontrack.model.structure.ProjectEntity} to get extended
 * by custom actions.
 */
public interface ProjectEntityActionExtension extends Extension {

    /**
     * Gets the action for this entity. Returns <code>null</code> if not
     * applicable or not authorised.
     */
    Optional<Action> getAction(ProjectEntity entity);

}
