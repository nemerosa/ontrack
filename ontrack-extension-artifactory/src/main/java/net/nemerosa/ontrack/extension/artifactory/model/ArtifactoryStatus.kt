package net.nemerosa.ontrack.extension.artifactory.model

import java.time.LocalDateTime

data class ArtifactoryStatus(
    val name: String,
    val user: String,
    val timestamp: LocalDateTime,
)