package net.nemerosa.ontrack.model.structure;

import java.util.List;

public interface Decorator {

    /**
     * Gets a list of decorations for this entity.
     *
     * @param entity Entity
     * @return A list of decorations to apply or empty if none.
     */
    List<Decoration> getDecorations(ProjectEntity entity);

}
