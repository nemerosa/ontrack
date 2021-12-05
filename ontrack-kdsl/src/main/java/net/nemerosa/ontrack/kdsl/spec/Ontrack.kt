package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.FindBranchByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.FindByBuildByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.FindProjectByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class Ontrack(connector: Connector) : Connected(connector) {

    /**
     * Getting a project using its name
     *
     * @param name Name to look for
     * @return Project or null if not found
     */
    fun findProjectByName(name: String): Project? = graphqlConnector.query(
        FindProjectByNameQuery(name)
    )?.projects()?.firstOrNull()
        ?.fragments()?.projectFragment()?.run {
            Project(
                connector = connector,
                id = id().toUInt(),
                name = name()!!,
                description = description(),
            )
        }

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
        ?.fragments()?.branchFragment()?.run {
            Branch(
                connector = connector,
                id = id().toUInt(),
                name = name()!!,
                description = description(),
                disabled = disabled(),
            )
        }

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
        ?.fragments()?.buildFragment()?.run {
            Build(
                connector = connector,
                id = id().toUInt(),
                name = name()!!,
                description = description(),
            )
        }

}