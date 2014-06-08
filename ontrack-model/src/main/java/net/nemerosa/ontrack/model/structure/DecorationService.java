package net.nemerosa.ontrack.model.structure;

import java.util.List;

public interface DecorationService {

    /**
     * Gets the list of decorations for an entity.
     *
     * @param entity Entity to decorate
     * @return List of its decorations
     */
    List<Decoration> getDecorations(ProjectEntity entity);

}
