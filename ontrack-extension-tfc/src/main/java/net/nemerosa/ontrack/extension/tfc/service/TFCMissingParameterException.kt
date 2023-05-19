package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.model.exceptions.InputException

class TFCMissingParameterException(param: String) : InputException(
        """TFC parameter is required: $param"""
)