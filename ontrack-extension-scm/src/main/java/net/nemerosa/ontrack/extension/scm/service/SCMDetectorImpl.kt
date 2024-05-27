package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class SCMDetectorImpl(
    private val extensionManager: ExtensionManager,
) : SCMDetector {

    private val extensions: Collection<SCMExtension> by lazy {
        extensionManager.getExtensions(SCMExtension::class.java)
    }

    override fun getSCM(project: Project): SCM? =
        extensions.firstNotNullOfOrNull {
            it.getSCM(project)
        }

}