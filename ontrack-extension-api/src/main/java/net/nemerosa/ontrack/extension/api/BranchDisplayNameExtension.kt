package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Branch

/**
 * Extension which allows to render the display name for
 * a [Branch].
 */
interface BranchDisplayNameExtension : Extension {

    fun getBranchDisplayName(branch: Branch): String?

}