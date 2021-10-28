package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload

interface IngestionHookProcessingService {

    fun process(payload: IngestionHookPayload)
    
}