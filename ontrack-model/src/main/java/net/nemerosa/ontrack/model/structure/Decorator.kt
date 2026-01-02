package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.extension.Extension

/**
 * Produces decorations
 * 
 * @param <T> Type of data associated with the decorations.
 */
interface Decorator<T> : Extension {
    /**
     * Gets a list of decorations for this entity.
     * 
     * @param entity Entity
     * @return A list of decorations to apply or empty if none.
     */
    fun getDecorations(entity: ProjectEntity): List<Decoration<T>>
}
