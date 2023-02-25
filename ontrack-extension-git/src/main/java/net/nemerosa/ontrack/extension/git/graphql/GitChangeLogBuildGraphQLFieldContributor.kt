package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.GitChangeLogCache
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GitChangeLogBuildGraphQLFieldContributor(
    private val gitChangeLogGQLType: GQLTypeGitChangeLog,
    private val structureService: StructureService,
    private val gitService: GitService,
    private val gitChangeLogCache: GitChangeLogCache,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BUILD) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("gitChangeLog")
                    .argument { a: GraphQLArgument.Builder ->
                        a.name("to")
                            .description("Name of the build to end the change log with. If not set, compares with the previous build.")
                            .type(Scalars.GraphQLString)
                    }
                    .type(gitChangeLogGQLType.typeRef)
                    .dataFetcher { env ->
                        val buildFrom: Build = env.getSource()
                        val to: String? = env.getArgument("to")
                        // Looking for the next build
                        val buildTo: Build = if (to.isNullOrBlank()) {
                            structureService.getPreviousBuild(buildFrom.id)
                                .getOrNull()
                                ?: return@dataFetcher null // No change log
                        } else {
                            structureService.findBuildByName(buildFrom.branch.project.name, buildFrom.branch.name, to)
                                .getOrNull()
                                ?: throw BuildNotFoundException(
                                    buildFrom.branch.project.name,
                                    buildFrom.branch.name,
                                    to
                                )
                        }
                        // Getting the change log
                        gitService.changeLog(
                            BuildDiffRequest(
                                from = buildFrom.id,
                                to = buildTo.id,
                            )
                        ).apply {
                            gitChangeLogCache.put(this)
                        }
                    }
                    .build()
            )
        } else {
            null
        }

}