package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.structure.Build

class SlotPipelineBuildNotEligibleException(
    slot: Slot,
    build: Build,
): InputException(
    """
        Build ${build.entityDisplayName} is not eligible for slot $slot.  
    """.trimIndent()
)