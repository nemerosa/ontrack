package net.nemerosa.ontrack.extension.av.postprocessing

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.model.extension.Extension

/**
 * Service responsible to post process a version upgrade in a branch.
 *
 * @param T Configuration type
 */
interface PostProcessing<T> : Extension {

    /**
     * ID of the service
     */
    val id: String

    /**
     * Display name of the service
     */
    val name: String

    /**
     * Post processing call.
     *
     * @param config Configuration of the post processing service
     * @param autoVersioningOrder Auto versioning order being processed
     * @param repositoryUri URI of the repository being processed
     * @param upgradeBranch Remote branch already containing the upgraded version
     */
    fun postProcessing(
        config: T,
        autoVersioningOrder: AutoVersioningOrder,
        repositoryUri: String,
        upgradeBranch: String,
    )

    /**
     * Given the configuration as JSON, parses it and validates it.
     */
    fun parseAndValidate(config: JsonNode?): T

}
