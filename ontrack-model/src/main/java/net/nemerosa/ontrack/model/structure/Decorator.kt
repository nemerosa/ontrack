package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.model.extension.Extension;

import java.util.List;

/**
 * Produces decorations
 *
 * @param <T> Type of data associated with the decorations.
 */
public interface Decorator<T> extends Extension {

    /**
     * Gets a list of decorations for this entity.
     *
     * @param entity Entity
     * @return A list of decorations to apply or empty if none.
     */
    List<Decoration<T>> getDecorations(ProjectEntity entity);

}
