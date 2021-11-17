package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector

class Ontrack(connector: Connector) : Connected(connector) {

    /**
     * Getting a project using its name
     *
     * @param name Name to look for
     * @return Project or null if not found
     */
    fun findProjectByName(name: String): Project? = TODO()

    /**
     * Getting a build using its name
     *
     * @param project Project name
     * @param branch Branch name
     * @param build Build name
     * @return Project or null if not found
     */
    fun findBuildByName(project: String, branch: String, build: String): Build? = TODO()

}