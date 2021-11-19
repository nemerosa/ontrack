package net.nemerosa.ontrack.kdsl.acceptance.tests.github.ingestion

import java.util.*

data class GitHubIngestionHookResponse(
    val message: String,
    val uuid: UUID?,
    val event: String,
    val processing: Boolean,
)