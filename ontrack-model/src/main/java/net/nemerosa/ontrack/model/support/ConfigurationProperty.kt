package net.nemerosa.ontrack.model.support

/**
 * Type of [property][net.nemerosa.ontrack.model.structure.PropertyType] which gives access to
 * a [configuration][Configuration].
 *
 * @param <T> Type of the configuration
 */
interface ConfigurationProperty<T : Configuration<T>> {

    /**
     * Access to the configuration.
     */
    val configuration: T

}