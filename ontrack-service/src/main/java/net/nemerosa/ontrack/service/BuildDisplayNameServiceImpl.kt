package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.BuildDisplayNameExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BuildDisplayNameServiceImpl(
    private val extensionManager: ExtensionManager
) : BuildDisplayNameService {

    override fun getFirstBuildDisplayName(build: Build): String? {
        val extensions = extensionManager.getExtensions(BuildDisplayNameExtension::class.java)
        return extensions.map { it.getBuildDisplayName(build) }.firstOrNull()
    }

    override fun findBuildByDisplayName(project: Project, name: String, onlyDisplayName: Boolean): Build? {
        val extensions = extensionManager.getExtensions(BuildDisplayNameExtension::class.java)
        return extensions.firstNotNullOfOrNull { it.findBuildByDisplayName(project, name, onlyDisplayName) }
    }
}