package net.nemerosa.ontrack.extension.av.postprocessing

/**
 * Information provided by some post-processing egine when it actually starts.
 *
 * Typically, a link to the job/pipeline doing the work.
 */
data class PostProcessingInfo(
    val data: Map<String, String>,
)
