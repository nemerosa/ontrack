package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.BranchDisplayNameExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.BranchNamePolicy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BranchDisplayNameServiceImpl(
    private val extensionManager: ExtensionManager
) : BranchDisplayNameService {

    @Deprecated("Will be removed in V5. Use the method with the branch name policy.")
    override fun getBranchDisplayName(branch: Branch): String {
        return getBranchDisplayName(branch, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
    }

    override fun getBranchDisplayName(branch: Branch, policy: BranchNamePolicy): String {
        val displayName: String? by lazy {
            extensionManager.getExtensions(BranchDisplayNameExtension::class.java)
                .firstNotNullOfOrNull { extension -> extension.getBranchDisplayName(branch) }
        }
        return when (policy) {
            BranchNamePolicy.NAME_ONLY -> branch.name
            BranchNamePolicy.DISPLAY_NAME_OR_NAME -> displayName ?: branch.name
            BranchNamePolicy.DISPLAY_NAME_ONLY -> displayName ?: error("No display name for $branch")
        }
    }
}