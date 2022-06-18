package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.BranchDisplayNameExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BranchDisplayNameServiceImpl(
        private val extensionManager: ExtensionManager
) : BranchDisplayNameService {

    override fun getBranchDisplayName(branch: Branch): String {
        val extendedName = extensionManager.getExtensions(BranchDisplayNameExtension::class.java)
            .firstNotNullOfOrNull { extension -> extension.getBranchDisplayName(branch) }
        return extendedName ?: branch.name
    }

}