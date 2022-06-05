package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Project

/**
 * Service used to get the [SCM] associated with a project, implemented by actual SCM extensions.
 */
interface SCMExtension : Extension {

    /**
     * Given a [project], returns its [SCM] interface if any.
     */
    fun getSCM(project: Project): SCM?

}