package net.nemerosa.ontrack.kdsl.spec.extension.github.autoversioning

class GitHubPostProcessingSettings(
    val config: String?,
    val repository: String?,
    val workflow: String?,
    val branch: String,
    val retries: Int,
    val retriesDelaySeconds: Int,
)
