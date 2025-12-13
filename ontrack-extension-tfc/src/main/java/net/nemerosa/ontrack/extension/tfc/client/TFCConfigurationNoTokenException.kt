package net.nemerosa.ontrack.extension.tfc.client

import net.nemerosa.ontrack.model.exceptions.InputException

class TFCConfigurationNoTokenException : InputException(
    "TFC token is required."
)