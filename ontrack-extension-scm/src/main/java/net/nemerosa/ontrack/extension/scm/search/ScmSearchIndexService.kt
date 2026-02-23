package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.Project

interface ScmSearchIndexService {

    /**
     * Launches an incremental indexation of the SCM commits and issues for this [project].
     *
     * @param project Project to index
     * @return Number of commits indexed
     */
    fun index(project: Project): Int

    /**
     * Gets a paginated list of commits for a project, from the newest to the oldest.
     */
    fun getCommits(project: Project, offset: Int, size: Int): PaginatedList<ScmIndexCommit>

    /**
     * Looks for all issues matching the given [key].
     */
    fun findIssues(key: String): List<ScmIndexIssue>

    /**
     * Looks for the last commit for a given project and issue key.
     */
    fun getIssueLastCommit(project: Project, key: String): ScmIndexCommit?

}