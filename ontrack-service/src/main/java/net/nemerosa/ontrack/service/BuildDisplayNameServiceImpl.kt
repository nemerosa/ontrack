package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.BuildDisplayNameExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BuildDisplayNameServiceImpl(
        private val extensionManager: ExtensionManager
) : BuildDisplayNameService {

    override fun getBuildDisplayName(build: Build): String {
        val extendedName = extensionManager.getExtensions(BuildDisplayNameExtension::class.java)
                .mapNotNull { extension -> extension.getBuildDisplayName(build) }
                .firstOrNull()
        return extendedName ?: build.name
    }

}