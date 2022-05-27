package net.nemerosa.ontrack.kdsl.spec

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLMissingDataException
import net.nemerosa.ontrack.kdsl.connector.graphql.checkData
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateProjectMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.FindBranchByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.FindByBuildByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.FindProjectByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class Ontrack(connector: Connector) : Connected(connector) {

    /**
     * Creates a project
     *
     * @param name Name of the project
     * @param description Description for the project
     */
    fun createProject(name: String, description: String): Project =
        graphqlConnector.mutate(
            CreateProjectMutation(
                name,
                Input.optional(description),
            )
        ) {
            it?.createProject()?.fragments()?.payloadUserErrors()?.convert()
        }?.checkData {
            it.createProject()?.project()
        }
            ?.fragments()?.projectFragment()?.toProject(this)
            ?: throw GraphQLMissingDataException("Did not get back the created project")

    /**
     * Getting a project using its name
     *
     * @param name Name to look for
     * @return Project or null if not found
     */
    fun findProjectByName(name: String): Project? = graphqlConnector.query(
        FindProjectByNameQuery(name)
    )?.projects()?.firstOrNull()
        ?.fragments()?.projectFragment()?.toProject(this)

    /**
     * Getting a branch using its name
     *
     * @param project Project name
     * @param branch Branch name
     * @return Branch or null if not found
     */
    fun findBranchByName(project: String, branch: String): Branch? = graphqlConnector.query(
        FindBranchByNameQuery(project, branch)
    )?.branches()?.firstOrNull()
        ?.fragments()?.branchFragment()?.toBranch(this@Ontrack)

    /**
     * Getting a build using its name
     *
     * @param project Project name
     * @param branch Branch name
     * @param build Build name
     * @return Build or null if not found
     */
    fun findBuildByName(project: String, branch: String, build: String): Build? = graphqlConnector.query(
        FindByBuildByNameQuery(project, branch, build)
    )?.builds()?.firstOrNull()
        ?.fragments()?.buildFragment()?.toBuild(this@Ontrack)

}