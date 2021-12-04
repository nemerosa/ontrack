package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Branch(
    val ref: String,
    val sha: String,
    val repo: BranchRepo,
)

@JsonIgnoreProperties(ignoreUnknown = true)
class BranchRepo(
    val name: String,
)
