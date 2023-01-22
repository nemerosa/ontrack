package net.nemerosa.ontrack.extension.casc.ui

import net.nemerosa.ontrack.model.exceptions.InputException

class CascUploadNotEnabledException: InputException(
    "Upload of Casc configuration is not enabled."
)