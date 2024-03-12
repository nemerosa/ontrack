package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.structure.Build

/**
 * Service used to get a change log between two builds in the same project.
 */
interface SCMChangeLogService {

    /**
     * Gets the boundaries for a change log.
     *
     * @param from Build boundary
     * @param to Build boundary
     * @param dependencies List of project links to follow to get a recursive change log
     * @param defaultQualifierFallback True if a qualified link does not exist and we need to fallback to the default qualifier
     */
    suspend fun getChangeLogBoundaries(
        from: Build,
        to: Build,
        dependencies: List<DependencyLink>,
        defaultQualifierFallback: Boolean = false,
    ): Pair<Build, Build>?

    /**
     * Gets a change log between two builds in the same project.
     *
     * @param from Build boundary
     * @param to Build boundary
     * @param dependencies List of project links to follow to get a recursive change log
     * @param defaultQualifierFallback True if a qualified link does not exist and we need to fallback to the default qualifier
     * @return Change log containing the commits and the issues or `null` if the change is empty
     */
    suspend fun getChangeLog(
        from: Build,
        to: Build,
        dependencies: List<DependencyLink> = emptyList(),
        defaultQualifierFallback: Boolean = false,
    ): SCMChangeLog?

}