package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import java.util.*

/**
 * Service used to get the SCM service associated with a branch, implemented by actual SCM extensions.
 */
@Deprecated("Prefer using SCMExtension")
interface SCMServiceProvider {
    /**
     * Gets the SCM service associated with a branch, or empty if none is available.
     *
     */
    @Deprecated("Use the method at project level")
    fun getScmService(branch: Branch): Optional<SCMService>

    /**
     * Gets the SCM service associated with a project, or empty if none is available.
     */
    @Deprecated("Use the getProjectScmService method")
    fun getScmService(project: Project): Optional<SCMService>

    /**
     * Gets the SCM service associated with a project, or null if none is available.
     */
    fun getProjectScmService(project: Project): SCMService?
}