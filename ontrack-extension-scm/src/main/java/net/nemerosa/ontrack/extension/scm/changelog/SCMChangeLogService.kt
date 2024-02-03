package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.structure.Build

/**
 * Service used to get a change log between two builds in the same project.
 */
interface SCMChangeLogService {

    /**
     * Gets a change log between two builds in the same project.
     *
     * @param from Build boundary
     * @param to Build boundary
     * @return Change log containing the commits and the issues
     */
    suspend fun getChangeLog(from: Build, to: Build): SCMChangeLog

}