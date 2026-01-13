package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig

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
        config: TemplatingSourceConfig,
        renderer: EventRenderer,
    ): String

    companion object {
        /**
         * Default field (null or empty)
         */
        const val DEFAULT_FIELD = "default"
    }

}