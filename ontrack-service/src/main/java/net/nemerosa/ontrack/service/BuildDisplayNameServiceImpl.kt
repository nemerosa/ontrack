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

    override fun getBuildDisplayName(build: Build): String {
        val extendedName = extensionManager.getExtensions(BuildDisplayNameExtension::class.java)
            .firstNotNullOfOrNull { extension -> extension.getBuildDisplayName(build) }
        return extendedName ?: build.name
    }

    override fun getEligibleBuildDisplayName(
        build: Build,
        defaultValue: (Build) -> String?,
    ): String? {
        val extensions = extensionManager.getExtensions(BuildDisplayNameExtension::class.java)
        // Gets the first extension which may return an eligible name
        val extension = extensions.find { it.mustProvideBuildName(build) }
        // If there is one, we ask its generated name, if not we return the build name as a default
        return if (extension != null) {
            extension.getBuildDisplayName(build)
        } else {
            defaultValue(build)
        }
    }

    override fun getFirstBuildDisplayName(build: Build): String? {
        val extensions = extensionManager.getExtensions(BuildDisplayNameExtension::class.java)
        return extensions.map { it.getBuildDisplayName(build) }.firstOrNull()
    }

    override fun findBuildByDisplayName(project: Project, name: String, onlyDisplayName: Boolean): Build? {
        val extensions = extensionManager.getExtensions(BuildDisplayNameExtension::class.java)
        return extensions.firstNotNullOfOrNull { it.findBuildByDisplayName(project, name, onlyDisplayName) }
    }
}