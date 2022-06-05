package net.nemerosa.ontrack.extension.av.postprocessing

/**
 * Access to the available [PostProcessing] services.
 */
interface PostProcessingRegistry {

    /**
     * Gets a [PostProcessing] using its ID
     *
     * @param id ID of the [PostProcessing] to find
     * @return `null` if not found
     */
    fun <T> getPostProcessingById(id: String): PostProcessing<T>?

    /**
     * Gets the list of all available [PostProcessing] services.
     */
    val allPostProcessings: List<PostProcessing<*>>

}