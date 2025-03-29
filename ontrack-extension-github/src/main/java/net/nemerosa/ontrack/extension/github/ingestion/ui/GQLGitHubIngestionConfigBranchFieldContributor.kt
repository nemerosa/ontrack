package net.nemerosa.ontrack.extension.github.ingestion.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLGitHubIngestionConfigBranchFieldContributor(
    private val gqlTypeGitHubIngestionConfig: GQLTypeGitHubIngestionConfig,
    private val configService: ConfigService,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? = if (projectEntityType == ProjectEntityType.BRANCH) {
        listOf(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("gitHubIngestionConfig")
                .description("Registered GitHub ingestion configuration")
                .type(gqlTypeGitHubIngestionConfig.typeRef)
                .dataFetcher { env ->
                    val branch: Branch = env.getSource()!!
                    configService.findConfig(branch)
                }
                .build()
        )
    } else {
        null
    }
}