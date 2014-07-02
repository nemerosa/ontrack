package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.security.Action;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

/**
 * Allows a {@link net.nemerosa.ontrack.model.structure.ProjectEntity} to get extended
 * by custom actions.
 */
public interface ProjectEntityActionExtension extends Extension {

    /**
     * Gets the action for this entity. Returns <code>null</code> if not
     * applicable or not authorised.
     */
    Action getAction(ProjectEntity entity);

}
