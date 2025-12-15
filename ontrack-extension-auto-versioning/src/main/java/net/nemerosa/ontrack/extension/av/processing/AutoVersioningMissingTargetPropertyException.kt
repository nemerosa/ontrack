package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.model.exceptions.InputException

class AutoVersioningMissingTargetPropertyException(message: String = "Missing target property.") :
    InputException(message)
