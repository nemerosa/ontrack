package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.GitChangeLogCache
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GitChangeLogBranchGraphQLFieldContributor(
    private val gitChangeLogGQLType: GQLTypeGitChangeLog,
    private val structureService: StructureService,
    private val gitService: GitService,
    private val gitChangeLogCache: GitChangeLogCache,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BRANCH) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("gitChangeLog")
                    .argument {
                        it.name("from")
                            .description("Name of the build to start the change log with")
                            .type(GraphQLNonNull(Scalars.GraphQLString))
                    }
                    .argument { a: GraphQLArgument.Builder ->
                        a.name("to")
                            .description("Name of the build to end the change log with")
                            .type(GraphQLNonNull(Scalars.GraphQLString))
                    }
                    .type(gitChangeLogGQLType.typeRef)
                    .dataFetcher { env ->
                        val branch: Branch = env.getSource()!!
                        val from: String = env.getArgument("from")!!
                        val to: String = env.getArgument("to")!!
                        // Looking for the builds
                        val buildFrom = structureService.findBuildByName(branch.project.name, branch.name, from)
                            .getOrNull()
                            ?: throw BuildNotFoundException(branch.project.name, branch.name, from)
                        val buildTo = structureService.findBuildByName(branch.project.name, branch.name, to)
                            .getOrNull()
                            ?: throw BuildNotFoundException(branch.project.name, branch.name, to)
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