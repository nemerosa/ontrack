package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

/**
 * Contributes the `displayName` field to the `Build` type to display the build's display name.
 */
@Component
class GQLBuildDisplayNameFieldContributor(
    private val buildDisplayNameService: BuildDisplayNameService,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition> {
        if (projectEntityType == ProjectEntityType.BUILD) {
            return listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("displayName")
                    .description("Display name for this build or the build's name if not available")
                    .type(GraphQLNonNull(GraphQLString))
                    .dataFetcher { env ->
                        val build: Build = env.getSource()!!
                        buildDisplayNameService.getFirstBuildDisplayName(build) ?: build.name
                    }
                    .build()
            )
        } else {
            return emptyList()
        }
    }
}
