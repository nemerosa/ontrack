package net.nemerosa.ontrack.extension.av.postprocessing

/**
 * Defines an exception in the case of a post-processing failure.
 */
interface PostProcessingFailureException {

    /**
     * Link to the source of the error (job for example).
     */
    val link: String

}