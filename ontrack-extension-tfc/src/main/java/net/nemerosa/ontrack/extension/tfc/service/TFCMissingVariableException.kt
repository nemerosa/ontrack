package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.model.exceptions.InputException

class TFCMissingVariableException(varName: String, workspaceId: String) : InputException(
    """Variable "$varName" is not defined in workspace $workspaceId."""
)
