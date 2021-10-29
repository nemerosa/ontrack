package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.model.exceptions.NotFoundException
import java.util.*

class IngestionHookPayloadUUIDNotFoundException(uuid: UUID) : NotFoundException(
    "Ingestion payload with UUID = $uuid not found."
)