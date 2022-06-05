package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class SCMDetectorImpl(
    private val extensionManager: ExtensionManager,
) : SCMDetector {

    override fun getSCM(project: Project): SCM? =
        extensionManager.getExtensions(SCMExtension::class.java).firstNotNullOfOrNull {
            it.getSCM(project)
        }

}