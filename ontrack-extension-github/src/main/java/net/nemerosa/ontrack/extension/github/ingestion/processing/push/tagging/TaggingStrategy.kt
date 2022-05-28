package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

/**
 * Strategy to retrieve a build to tag.
 *
 * @param C Type of the configuration
 */
interface TaggingStrategy<C> {

    /**
     * Unique type identified
     */
    val type: String

}
