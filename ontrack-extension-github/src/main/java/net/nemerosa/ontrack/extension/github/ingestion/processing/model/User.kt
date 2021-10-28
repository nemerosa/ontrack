package net.nemerosa.ontrack.extension.github.ingestion.processing.model

@JsonIgnoreProperties(ignoreUnknown = true)
class User(
    val login: String,
)