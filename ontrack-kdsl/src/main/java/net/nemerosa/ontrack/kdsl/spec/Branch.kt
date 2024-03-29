package net.nemerosa.ontrack.kdsl.spec

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLMissingDataException
import net.nemerosa.ontrack.kdsl.connector.graphql.checkData
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateBuildMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreatePromotionLevelMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateValidationStampMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.RunInfoInput
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Representation of a branch.
 *
 * @property connector Ontrack connector
 * @property project Parent project
 * @property id Branch ID
 * @property name Branch name
 * @property description Branch description
 * @property disabled Branch state
 */
class Branch(
    connector: Connector,
    val project: Project,
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
     * Create a validation stamp inside this branch.
     *
     * @param name Name of the validation stamp to create
     * @param description Description of the validation stamp
     * @return Created validation stamp
     */
    fun createValidationStamp(
        name: String,
        description: String,
        dataType: String? = null,
        dataTypeConfig: Any? = null,
    ): ValidationStamp =
        graphqlConnector.mutate(
            CreateValidationStampMutation(
                id.toInt(),
                name,
                description,
                Input.fromNullable(dataType),
                Input.fromNullable(dataTypeConfig?.asJson())
            )
        ) {
            it?.createValidationStampById()?.fragments()?.payloadUserErrors()?.convert()
        }
            ?.checkData { it.createValidationStampById()?.validationStamp() }
            ?.fragments()?.validationStampFragment()?.toValidationStamp(this)
            ?: throw GraphQLMissingDataException("Did not get back the created validation stamp")

    /**
     * Create a build inside this branch.
     *
     * @param name Name of the build to create
     * @param description Description of the build
     * @param runTime Run time in seconds
     * @return Created build
     */
    fun createBuild(
        name: String,
        description: String = "",
        runTime: Int? = null,
    ): Build =
        graphqlConnector.mutate(
            CreateBuildMutation(
                id.toInt(),
                name,
                description,
                Input.fromNullable(
                    runTime?.let {
                        RunInfoInput.builder()
                            .runTime(it)
                            .build()
                    }
                )
            )
        ) {
            it?.createBuild()?.fragments()?.payloadUserErrors()?.convert()
        }
            ?.checkData { it.createBuild()?.build() }
            ?.fragments()?.buildFragment()?.toBuild(this)
            ?: throw GraphQLMissingDataException("Did not get back the created build")

}
