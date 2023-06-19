package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.DecorationService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLProjectEntityDecorationListFieldContributor(
        private val decorationService: DecorationService,
        private val decoration: GQLTypeDecoration,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
            projectEntityClass: Class<out ProjectEntity>,
            projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? {
        return listOf<GraphQLFieldDefinition>(
                GraphQLFieldDefinition.newFieldDefinition()
                        .name("decorations")
                        .description("List of decorations")
                        .argument(
                                GraphQLArgument.newArgument()
                                        .name("type")
                                        .description("Fully qualified name of the decoration type")
                                        .type(Scalars.GraphQLString)
                                        .build()
                        )
                        .type(listType(decoration.typeRef))
                        .dataFetcher(projectEntityDecorationsDataFetcher(projectEntityClass))
                        .build()
        )
    }

    private fun projectEntityDecorationsDataFetcher(projectEntityClass: Class<out ProjectEntity>) =
            DataFetcher { environment ->
                val o = environment.getSource<Any>()
                if (projectEntityClass.isInstance(o)) {
                    // Filters
                    val typeFilter: String? = environment.getArgument("type")
                    // Gets the raw list
                    decorationService.getDecorations(o as ProjectEntity)
                            .filter { property: Decoration<*> ->
                                typeFilter?.let {
                                    it == property.decorationType
                                } ?: true
                            }
                } else {
                    return@DataFetcher null
                }
            }
}
