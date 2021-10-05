package net.nemerosa.ontrack.model.support

import com.fasterxml.jackson.annotation.JsonIgnore

interface Configuration<T : Configuration<T>> {

    /**
     * Name of this configuration
     */
    val name: String

    /**
     * Gets the descriptor for this configuration
     */
    @get:JsonIgnore
    val descriptor: ConfigurationDescriptor

    /**
     * Utility method that erases any sensitive data from this configuration
     * and returns new one.
     */
    fun obfuscate(): T

}