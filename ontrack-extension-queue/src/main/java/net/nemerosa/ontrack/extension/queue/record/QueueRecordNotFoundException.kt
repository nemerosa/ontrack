package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class QueueRecordNotFoundException(id: String): NotFoundException(
    "Cannot find queue record with id = $id"
)