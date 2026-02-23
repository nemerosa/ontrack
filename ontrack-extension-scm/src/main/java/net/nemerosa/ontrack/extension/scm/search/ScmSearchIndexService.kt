package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.model.structure.Project

interface ScmSearchIndexService {

    /**
     * Launches an incremental indexation of the SCM commits and issues for this [project].
     *
     * @param project Project to index
     * @return Number of commits indexed
     */
    fun index(project: Project): Int

}