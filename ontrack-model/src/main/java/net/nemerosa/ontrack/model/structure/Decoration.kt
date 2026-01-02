package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription

/**
 * Decoration for an entity.
 * 
 * @param <T> Type of data contained by the entity
 * @param decorator Which [Decorator] has created this decoration
 * @param data      Data associated with the decoration
 * @param error     Error data
</T> */
data class Decoration<T>(val decorator: Decorator<T>, val data: T?, val error: String?) {
    /**
     * Gets the decoration type for the decorator name.
     */
    val decorationType: String = decorator.javaClass.getName()

    /**
     * Extension feature description
     */
    val feature: ExtensionFeatureDescription = decorator.feature.featureDescription

    companion object {
        /**
         * Basic construction. Only the data is required
         */
        fun <T> of(decorator: Decorator<T>, data: T): Decoration<T> {
            return Decoration(decorator, data, null)
        }

        /**
         * Basic construction. With an error
         */
        fun <T> error(decorator: Decorator<T>, error: String): Decoration<T> {
            return Decoration(decorator, null, error)
        }
    }
}
