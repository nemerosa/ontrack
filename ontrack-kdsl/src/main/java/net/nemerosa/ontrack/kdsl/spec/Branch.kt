package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLMissingDataException
import net.nemerosa.ontrack.kdsl.connector.graphql.checkData
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateBuildMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreatePromotionLevelMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Representation of a branch.
 *
 * @property connector Ontrack connector
 * @property id Branch ID
 * @property name Branch name
 * @property description Branch description
 * @property disabled Branch state
 */
class Branch(
    connector: Connector,
    id: UInt,
    val name: String,
    val description: String?,
    val disabled: Boolean,
) : ProjectEntity(connector, ProjectEntityType.BRANCH, id) {

    /**
     * Create a promotion level inside this branch.
     *
     * @param name Name of the promotion level to create
     * @param description Description of the promotion level
     * @return Created promotion level
     */
    fun createPromotionLevel(
        name: String,
        description: String,
    ): PromotionLevel =
        graphqlConnector.mutate(
            CreatePromotionLevelMutation(
                id.toInt(),
                name,
                description
            )
        ) {
            it?.createPromotionLevelById()?.fragments()?.payloadUserErrors()?.convert()
        }
            ?.checkData { it.createPromotionLevelById()?.promotionLevel() }
            ?.fragments()?.promotionLevelFragment()?.toPromotionLevel(this)
            ?: throw GraphQLMissingDataException("Did not get back the created promotion level")

    /**
     * Create a build inside this branch.
     *
     * @param name Name of the build to create
     * @param description Description of the build
     * @return Created build
     */
    fun createBuild(
        name: String,
        description: String,
    ): Build =
        graphqlConnector.mutate(
            CreateBuildMutation(
                id.toInt(),
                name,
                description
            )
        ) {
            it?.createBuild()?.fragments()?.payloadUserErrors()?.convert()
        }
            ?.checkData { it.createBuild()?.build() }
            ?.fragments()?.buildFragment()?.toBuild(this)
            ?: throw GraphQLMissingDataException("Did not get back the created build")

}
