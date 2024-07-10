package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.BranchNamePolicy

data class BranchVersionTemplatingSourceParameters(
    @APIDescription("Which branch name to use")
    val policy: BranchNamePolicy = BranchNamePolicy.DISPLAY_NAME_OR_NAME,
    @APIDescription("Default value to use")
    val default: String = ""
)
