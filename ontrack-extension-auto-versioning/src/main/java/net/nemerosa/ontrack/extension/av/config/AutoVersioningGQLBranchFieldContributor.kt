package net.nemerosa.ontrack.extension.av.config

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class AutoVersioningGQLBranchFieldContributor(
    private val gqlTypeAutoVersioningConfig: GQLTypeAutoVersioningConfig,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? = if (projectEntityType == ProjectEntityType.BRANCH) {
        listOf(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("autoVersioningConfig")
                .description("Auto versioning configuration")
                .type(gqlTypeAutoVersioningConfig.typeRef)
                .dataFetcher { env ->
                    val branch: Branch = env.getSource()
                    autoVersioningConfigurationService.getAutoVersioning(branch)
                }
                .build(),
            GraphQLFieldDefinition.newFieldDefinition()
                .name("autoVersioningConfigYAML")
                .description("Auto versioning configuration as YAML")
                .type(GraphQLString)
                .dataFetcher { env ->
                    val branch: Branch = env.getSource()
                    autoVersioningConfigurationService.getAutoVersioning(branch)?.let {
                        AutoVersioningConfigParser.toYaml(it)
                    }
                }
                .build(),
        )
    } else {
        null
    }

}