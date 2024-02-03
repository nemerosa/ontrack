package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.model.structure.Build

/**
 * Marks a [SCM] as being able to compute a change log.
 */
interface SCMChangeLogEnabled : SCM {

    /**
     * Gets the change log between two builds.
     *
     * Implementation notes:
     *
     * * the two builds are in the same project at the moment of the call.
     *
     * @param from Build boundary
     * @param to Build boundary
     * @return Change log containing the commits and the issues
     */
    // fun getChangeLog(from: Build, to: Build): SCMChangeLog

    /**
     * Given a [build][Build], returns a commit ID or any other reference which is suitable
     * for getting a change log.
     */
    fun getBuildCommit(build: Build): String?

    /**
     * Given two boundaries (as defined by the [getBuildCommit] function, returns
     * a list of commits between the two.
     */
    suspend fun getCommits(fromCommit: String, toCommit: String): List<SCMChangeLogCommit>

    /**
     * Gets the configured issue service for this SCM.
     */
    fun getConfiguredIssueService(): ConfiguredIssueService?

}