package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.events.EventRenderer

interface TemplatingFunction {

    fun render(
        config: TemplatingSourceConfig,
        context: Map<String, Any>,
        renderer: EventRenderer,
        expressionResolver: (expression: String) -> String,
    ): String

    val id: String

}