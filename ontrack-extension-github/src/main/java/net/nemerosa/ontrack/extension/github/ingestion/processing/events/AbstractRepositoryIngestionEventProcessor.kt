package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.extension.github.ingestion.processing.AbstractIngestionEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.model.structure.StructureService

abstract class AbstractRepositoryIngestionEventProcessor<T : AbstractRepositoryPayload>(
    protected val structureService: StructureService,
) : AbstractIngestionEventProcessor<T>() {

}

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class AbstractRepositoryPayload(
    val repository: Repository,
)
