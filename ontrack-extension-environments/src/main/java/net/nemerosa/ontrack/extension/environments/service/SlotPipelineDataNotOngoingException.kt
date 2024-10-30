package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.model.exceptions.InputException

class SlotPipelineDataNotOngoingException : InputException(
    "Data can be set on a pipeline only when it's ongoing."
)