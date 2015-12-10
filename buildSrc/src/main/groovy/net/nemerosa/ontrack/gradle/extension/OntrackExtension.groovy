package net.nemerosa.ontrack.gradle.extension

/**
 * Configuration of the extension
 */
class OntrackExtension {

    /**
     * ID of the extension (required)
     */
    String id

    /**
     * DSL access
     */
    void id(String value) {
        this.id = value
    }

}
