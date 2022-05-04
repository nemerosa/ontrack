package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLMissingDataException
import net.nemerosa.ontrack.kdsl.connector.graphql.checkData
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.BuildValidationRunsQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreatePromotionRunMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Representation of a build.
 *
 * @property connector Ontrack connector
 * @property id Build ID
 * @property name Build name
 * @property description Build description
 */
class Build(
    connector: Connector,
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

}
