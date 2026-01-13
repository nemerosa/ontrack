package net.nemerosa.ontrack.extension.scm.changelog

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(using = SemanticChangeLogSectionDeserializer::class)
data class SemanticChangeLogSection(
    val type: String,
    val title: String,
)
