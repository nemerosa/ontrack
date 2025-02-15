package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.events.EventRenderer

/**
 * Component responsible to render the template for a deployment.
 */
interface DeploymentTemplatingContextFieldHandler {

    /**
     * Field to handle
     */
    val field: String

    /**
     * Rendering the deployment
     */
    fun render(
        deployment: SlotPipeline,
        config: Map<String, String>,
        renderer: EventRenderer,
    ): String

    companion object {
        /**
         * Default field (null or empty)
         */
        const val DEFAULT_FIELD = "default"
    }

}