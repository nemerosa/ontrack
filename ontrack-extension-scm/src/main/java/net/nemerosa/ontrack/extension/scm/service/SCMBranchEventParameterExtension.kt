package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.api.EventParameterExtension
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.stereotype.Component

@Component
class SCMBranchEventParameterExtension(
    extensionFeature: SCMExtensionFeature,
    private val scmDetector: SCMDetector,
) : AbstractExtension(extensionFeature), EventParameterExtension {

    override fun additionalTemplateParameters(entity: ProjectEntity): Map<String, String> {
        if (entity is Branch) {
            val scm = scmDetector.getSCM(entity.project) ?: return emptyMap()
            val scmBranch = scm.getSCMBranch(entity) ?: return emptyMap()
            return mapOf(
                "scmBranch" to scmBranch
            )
        } else {
            return emptyMap()
        }
    }

}