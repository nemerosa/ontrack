package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.AbstractRepositoryPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.PullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository

@JsonIgnoreProperties(ignoreUnknown = true)
class PRPayload(
    repository: Repository,
    val action: PRPayloadAction,
    @JsonProperty("pull_request")
    val pullRequest: PullRequest,
) : AbstractRepositoryPayload(
    repository,
)
