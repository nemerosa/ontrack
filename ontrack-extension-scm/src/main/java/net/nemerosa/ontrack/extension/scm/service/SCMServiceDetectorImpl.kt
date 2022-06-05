package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component
import java.util.*

@Component
class SCMServiceDetectorImpl(
    private val scmServiceProviders: List<SCMServiceProvider>,
) : SCMServiceDetector {

    override fun getScmService(branch: Branch): Optional<SCMService> =
        getScmService(branch.project)

    override fun getScmService(project: Project): Optional<SCMService> =
        Optional.ofNullable(getProjectScmService(project))

    override fun getProjectScmService(project: Project): SCMService? =
        scmServiceProviders.firstNotNullOfOrNull {
            it.getProjectScmService(project)
        }

}