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
     * @param projects List of project links to follow to get a recursive change log
     * @return Change log containing the commits and the issues or `null` if the change is empty
     */
    suspend fun getChangeLog(
        from: Build,
        to: Build,
        projects: List<ProjectLink> = emptyList(),
    ): SCMChangeLog?

}