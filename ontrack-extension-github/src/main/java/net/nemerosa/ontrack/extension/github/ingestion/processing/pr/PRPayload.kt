package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.AbstractRepositoryPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository

@JsonIgnoreProperties(ignoreUnknown = true)
class PRPayload(
    repository: Repository,
) : AbstractRepositoryPayload(
    repository,
)
