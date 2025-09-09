package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLFieldDefinition
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.extension.scm.changelog.DependencyLink
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.intArgument
import net.nemerosa.ontrack.graphql.support.stringListArgument
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * `scmChangeLog` query to get a change log between two builds.
 */
@Component
class GQLRootQuerySCMChangeLog(
    private val gqlTypeSCMChangeLog: GQLTypeSCMChangeLog,
    private val structureService: StructureService,
    private val scmChangeLogService: SCMChangeLogService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("scmChangeLog")
        .description("Query to get a change log between two builds.")
        .argument(intArgument(ARG_FROM, "ID of the build from", nullable = false))
        .argument(intArgument(ARG_TO, "ID of the build to", nullable = false))
        .argument(
            stringListArgument(
                ARG_PROJECTS,
                """
                    List of projects to follow one by one for a get deep change log. Each item
                    in the list is either a project name, or a project name and qualifier separated
                    by a colon (:).
                    """.trimIndent(),
                nullable = true
            )
        )
        .type(gqlTypeSCMChangeLog.typeRef)
        .dataFetcher { env ->
            val from: Int = env.getArgument(ARG_FROM)!!
            val to: Int = env.getArgument(ARG_TO)!!
            val projects: List<String>? = env.getArgument(ARG_PROJECTS)
            var buildFrom = structureService.getBuild(ID.of(from))
            var buildTo = structureService.getBuild(ID.of(to))

            // Inverting the boundaries so that "buildTo" is the most recent
            if (buildTo.signature.time < buildFrom.signature.time) {
                val tmp = buildFrom
                buildFrom = buildTo
                buildTo = tmp
            }

            runBlocking {
                scmChangeLogService.getChangeLog(
                    from = buildFrom,
                    to = buildTo,
                    dependencies = projects
                        ?.map { DependencyLink.parse(it) }
                        ?: emptyList(),
                )
            }
        }
        .build()

    companion object {
        const val ARG_FROM = "from"
        const val ARG_TO = "to"
        const val ARG_PROJECTS = "projects"
    }
}