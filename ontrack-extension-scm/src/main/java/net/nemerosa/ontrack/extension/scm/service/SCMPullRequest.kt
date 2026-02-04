package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.common.api.APIDescription

/**
 * Abstract representation of a pull request.
 *
 * @property id ID of the PR
 * @property name Display name for the PR
 * @property link HTML link to the PR
 */
data class SCMPullRequest(
    val id: String,
    val name: String,
    val link: String,
    @APIDescription("Status of the PR")
    val status: SCMPullRequestStatus,
)
