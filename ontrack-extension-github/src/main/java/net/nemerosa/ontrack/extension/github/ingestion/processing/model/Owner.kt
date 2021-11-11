package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.model.annotations.APIDescription

@JsonIgnoreProperties(ignoreUnknown = true)
data class Owner(
    @APIDescription("Login for the owner")
    val login: String,
)
