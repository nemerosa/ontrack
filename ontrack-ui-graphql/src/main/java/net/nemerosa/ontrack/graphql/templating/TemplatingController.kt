package net.nemerosa.ontrack.graphql.templating

import net.nemerosa.ontrack.model.events.EventRendererRegistry
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class TemplatingController(
    private val eventRendererRegistry: EventRendererRegistry,
) {

    @QueryMapping
    fun templatingRenderers(): List<TemplatingRenderer> =
        eventRendererRegistry.eventRenderers
            .map {
                TemplatingRenderer(
                    id = it.id,
                    name = it.name,
                )
            }
            .sortedBy {
                it.name
            }

    data class TemplatingRenderer(
        val id: String,
        val name: String,
    )

}