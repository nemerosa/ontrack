package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import org.springframework.stereotype.Component

@Component
class OpenedPRPayloadListener : AbstractPRPayloadListener(
    action = PRPayloadAction.opened
) {
    override fun process(payload: PRPayload, configuration: String?) {
        TODO("Not yet implemented")
    }
}