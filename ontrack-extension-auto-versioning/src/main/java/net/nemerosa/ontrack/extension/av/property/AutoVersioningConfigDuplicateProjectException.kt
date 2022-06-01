package net.nemerosa.ontrack.extension.av.property

import net.nemerosa.ontrack.model.exceptions.InputException

class AutoVersioningConfigDuplicateProjectException(
        duplicates: List<String>,
) : InputException(
    "It is not possible to configure a source project multiple times. Duplicate projects are: ${
        duplicates.joinToString(
            ", "
        )
    }"
)