package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLMissingDataException
import net.nemerosa.ontrack.kdsl.connector.graphql.checkData
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.paginate
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.BuildUsingQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.BuildValidationRunsQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreatePromotionRunMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.LinksBuildMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.LinksBuildInputItem
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.connector.support.PaginatedList
import net.nemerosa.ontrack.kdsl.connector.support.emptyPaginatedList

/**
 * Representation of a build.
 *
 * @property connector Ontrack connector
 * @property branch Parent branch
 * @property id Build ID
 * @property name Build name
 * @property description Build description
 */
class Build(
    connector: Connector,
    val branch: Branch,
    id: UInt,
    val name: String,
    val description: String?,
) : ProjectEntity(connector, ProjectEntityType.BUILD, id) {

    /**
     * Creates a promotion run for a build.
     *
     * @param promotion Name of the promotion level
     * @param description Description of the run
     * @return Promotion run
     */
    fun promote(
        promotion: String,
        description: String = "",
    ): PromotionRun = graphqlConnector.mutate(
        CreatePromotionRunMutation(
            id.toInt(),
            promotion,
            description
        )
    ) {
        it?.createPromotionRunById()?.fragments()?.payloadUserErrors()?.convert()
    }
        ?.checkData { it.createPromotionRunById()?.promotionRun() }
        ?.fragments()?.promotionRunFragment()?.toPromotionRun(this)
        ?: throw GraphQLMissingDataException("Did not get back the created promotion run")

    /**
     * Gets the list of validation runs for this build and a given validation stamp name (can be a regular expression).
     *
     * @param validationStamp Validation stamp name (can be a regular expression).
     * @param count Maximum number of runs to return
     */
    fun getValidationRuns(
        validationStamp: String,
        count: Int = 50,
    ): List<ValidationRun> =
        graphqlConnector.query(
            BuildValidationRunsQuery(id.toInt(), validationStamp, count)
        )?.builds()?.firstOrNull()?.validationRuns()?.map {
            it.fragments().validationRunFragment().toValidationRun(this)
        } ?: emptyList()

    /**
     * Gets the list of builds used by _this_ build.
     */
    fun getLinksUsing(
        offset: Int = 0,
        size: Int = 10,
    ): PaginatedList<Build> = graphqlConnector.query(
        BuildUsingQuery(id.toInt(), offset, size)
    )?.paginate(
        pageInfo = { it.builds().firstOrNull()?.using()?.pageInfo()?.fragments()?.pageInfoContent() },
        pageItems = { it.builds().firstOrNull()?.using()?.pageItems() }
    )?.map {
        it.fragments().buildFragment().toBuild(this@Build)
    } ?: emptyPaginatedList()

    /**
     * Links this build to other builds using their project names and their names.
     */
    fun linksTo(links: Map<String, String>) {
        graphqlConnector.mutate(
            LinksBuildMutation(
                branch.project.name,
                name,
                links.map { (project, name) ->
                    LinksBuildInputItem.builder()
                        .project(project)
                        .build(name)
                        .build()
                }
            )
        ) {
            it?.linksBuild()?.fragments()?.payloadUserErrors()?.convert()
        }
    }

    /**
     * See [linksTo].
     */
    fun linksTo(vararg links: Pair<String, String>) {
        linksTo(links.toMap())
    }

}
