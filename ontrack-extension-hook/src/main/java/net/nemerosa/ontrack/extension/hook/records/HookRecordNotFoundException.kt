package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class HookRecordNotFoundException(recordId: String) : NotFoundException(
        """Hook record with ID = $recordId was not found."""
)
