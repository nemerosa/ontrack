package net.nemerosa.ontrack.extension.casc.ui

import net.nemerosa.ontrack.extension.casc.upload.CascUploadConstants
import net.nemerosa.ontrack.model.exceptions.InputException

class CascUploadWrongTypeException(
    actualType: String?,
): InputException(
    "Only ${CascUploadConstants.TYPE} is accepted for Casc but received $actualType."
)