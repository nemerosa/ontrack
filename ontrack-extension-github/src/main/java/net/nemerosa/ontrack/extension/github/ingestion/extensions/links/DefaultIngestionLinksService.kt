package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DefaultIngestionLinksService(
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val storage: IngestionHookPayloadStorage,
    private val queue: IngestionHookQueue,
    private val securityService: SecurityService,
    private val ingestionLinksEventProcessor: IngestionLinksEventProcessor,
) : IngestionLinksService {
    override fun ingestLinks(input: AbstractGitHubIngestionLinksInput): UUID {
        // Checks the access rights on the target project
        val repository = Repository.stub(
            owner = input.owner,
            name = input.repository,
        )
        val project = ingestionModelAccessService.findProjectFromRepository(repository)
        if (project != null) {
            securityService.checkProjectFunction(project, ProjectConfig::class.java)
        } else {
            securityService.checkGlobalFunction(ProjectCreation::class.java)
        }
        // Creates the payload
        val payload = IngestionHookPayload(
            gitHubDelivery = "",
            gitHubEvent = IngestionLinksEventProcessor.EVENT,
            gitHubHookID = 0,
            gitHubHookInstallationTargetID = 0,
            gitHubHookInstallationTargetType = "",
            payload = input.toPayload().asJson(),
            repository = repository,
        )
        securityService.asAdmin {
            // Stores it
            storage.store(payload, ingestionLinksEventProcessor.getPayloadSource(payload))
            // Pushes it on the queue
            queue.queue(payload)
        }
        // OK
        return payload.uuid
    }
}