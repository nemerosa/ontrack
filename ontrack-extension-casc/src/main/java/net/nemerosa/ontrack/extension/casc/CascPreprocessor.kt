package net.nemerosa.ontrack.extension.casc

/**
 * Preprocessing of raw YAML Casc files.
 */
interface CascPreprocessor {

    fun process(yaml: String): String

}