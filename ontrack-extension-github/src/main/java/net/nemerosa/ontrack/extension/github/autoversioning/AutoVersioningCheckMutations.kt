package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionEventService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

/**
 * Mutations used to request auto versioning check.
 */
@Component
class AutoVersioningCheckMutations(
    private val ingestionEventService: IngestionEventService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        /**
         * Getting the build by run ID
         */
        simpleMutation(
            name = "gitHubCheckAutoVersioningByRunId",
            description = "Checks the dependencies based on the auto versioning information.",
            input = CheckAutoVersioningByRunIdInput::class,
            outputName = "payload",
            outputType = CheckAutoVersioningOutput::class,
            outputDescription = "Payload being processed in the background"
        ) { input ->
            val payload = AutoVersioningCheckDataPayload(
                owner = input.owner,
                repository = input.repository,
                runId = input.runId,
            )
            val uuid = ingestionEventService.sendIngestionEvent(
                event = AutoVersioningCheckEventProcessor.EVENT,
                owner = input.owner,
                repository = input.repository,
                payload = payload.asJson(),
                payloadSource = payload.getSource(),
            )
            CheckAutoVersioningOutput(uuid.toString())
        },
    )
}
