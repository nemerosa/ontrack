package net.nemerosa.ontrack.graphql.templating

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.events.EventRendererRegistry
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.templating.TemplatingService
import org.springframework.stereotype.Component

@Component
class RenderBuildGraphQLFieldContributor(
    private val templatingService: TemplatingService,
    private val eventRendererRegistry: EventRendererRegistry
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BUILD) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("render")
                    .description("Renders a template for this build")
                    .type(GraphQLString.toNotNull())
                    .argument(stringArgument(ARG_FORMAT, "Format of the rendering", nullable = false))
                    .argument(stringArgument(ARG_TEMPLATE, "Template for the rendering", nullable = false))
                    .dataFetcher { env ->
                        val build: Build = env.getSource()!!
                        val format: String = env.getArgument(ARG_FORMAT)!!
                        val template: String = env.getArgument(ARG_TEMPLATE)!!

                        // Renderer
                        val renderer = eventRendererRegistry.findEventRendererById(format)
                            ?: PlainEventRenderer.INSTANCE

                        // Rendering context
                        val context = mapOf(
                            "build" to build,
                            "branch" to build.branch,
                            "project" to build.project,
                        )

                        // Rendering
                        templatingService.render(
                            template = template,
                            context = context,
                            renderer = renderer,
                        )
                    }
                    .build()
            )
        } else {
            null
        }

    companion object {
        private const val ARG_FORMAT = "format"
        private const val ARG_TEMPLATE = "template"
    }

}