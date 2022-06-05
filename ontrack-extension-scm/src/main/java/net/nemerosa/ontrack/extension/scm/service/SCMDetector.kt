package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.structure.Project

/**
 * Getting the [SCM] interface for projects.
 */
interface SCMDetector {

    /**
     * Given a [project], returns its [SCM] interface if any.
     */
    fun getSCM(project: Project): SCM?

}