package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLMissingDataException
import net.nemerosa.ontrack.kdsl.connector.graphql.checkData
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.BranchListQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateBranchMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.DeleteProjectByIdMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Representation of a project.
 *
 * @property id Project ID
 * @property name Project name
 * @property description Project description
 */
class Project(
    connector: Connector,
    id: UInt,
    val name: String,
    val description: String?,
) : ProjectEntity(connector, ProjectEntityType.PROJECT, id) {
    /**
     * Deletes this project
     */
    fun delete() {
        graphqlConnector.mutate(
            DeleteProjectByIdMutation(id.toInt())
        ) {
            it?.deleteProject?.payloadUserErrors?.convert()
        }
    }

    /**
     * Create a branch inside this project.
     *
     * @param name Name of the branch to create
     * @param description Description of the branch
     * @return Created branch
     */
    fun createBranch(
        name: String,
        description: String,
    ): Branch =
        graphqlConnector.mutate(
            CreateBranchMutation(
                id.toInt(),
                name,
                description
            )
        ) {
            it?.createBranch?.payloadUserErrors?.convert()
        }
            ?.checkData { it.createBranch?.branch }
            ?.branchFragment?.toBranch(this)
            ?: throw GraphQLMissingDataException("Did not get back the created branch")


    /**
     * Gets a list of branches for this project.
     */
    fun branchList(): List<Branch> =
        graphqlConnector.query(
            BranchListQuery(id.toInt())
        )?.project?.branches?.map {
            it.branchFragment.toBranch(this)
        } ?: emptyList()

}
