package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;

public interface DecorationExtension extends Extension {

    /**
     * Scope of the decorator
     *
     * @return List of {@link net.nemerosa.ontrack.model.structure.ProjectEntityType} this decorator can apply to
     */
    EnumSet<ProjectEntityType> getScope();

    /**
     * Gets a decoration for this entity.
     *
     * @param entity Entity
     * @return A decoration to apply or <code>null</code> if none.
     */
    Decoration getDecoration(ProjectEntity entity);

}
