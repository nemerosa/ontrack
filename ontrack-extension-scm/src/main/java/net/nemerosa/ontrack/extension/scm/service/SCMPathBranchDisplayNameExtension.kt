package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.api.BranchDisplayNameExtension
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

@Component
class SCMPathBranchDisplayNameExtension(
    extensionFeature: SCMExtensionFeature,
    private val scmDetector: SCMDetector,
) : AbstractExtension(extensionFeature), BranchDisplayNameExtension {
    override fun getBranchDisplayName(branch: Branch): String? =
        scmDetector.getSCM(branch.project)
            ?.getSCMBranch(branch)
}