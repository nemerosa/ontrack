package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component
import java.util.*

/**
 * Placeholder for the SCM service providers.
 *
 * @see SCMServiceDetectorImpl
 */
@Component
class NOPSCMServiceProvider : SCMServiceProvider {

    override fun getScmService(branch: Branch): Optional<SCMService> = Optional.empty()

    override fun getScmService(project: Project): Optional<SCMService> = Optional.empty()

    override fun getProjectScmService(project: Project): SCMService? = null
}