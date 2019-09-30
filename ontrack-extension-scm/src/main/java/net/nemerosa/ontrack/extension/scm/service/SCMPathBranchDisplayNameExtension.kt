package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.BranchDisplayNameExtension
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

@Component
class SCMPathBranchDisplayNameExtension(
        extensionFeature: SCMExtensionFeature,
        private val scmServiceDetector: SCMServiceDetector
) : AbstractExtension(extensionFeature), BranchDisplayNameExtension {
    override fun getBranchDisplayName(branch: Branch): String? =
            scmServiceDetector.getScmService(branch).getOrNull()
                    ?.getSCMPathInfo(branch)?.getOrNull()
                    ?.branch
}