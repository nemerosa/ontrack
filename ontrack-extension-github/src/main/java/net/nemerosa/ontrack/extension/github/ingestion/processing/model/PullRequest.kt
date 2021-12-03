package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class PullRequest(
    val number: Int,
    val state: PullRequestState,
    val head: Branch,
    val base: Branch,
    val merged: Boolean,
    val mergeable: Boolean?,
)
