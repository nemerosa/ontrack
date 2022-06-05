package net.nemerosa.ontrack.extension.scm.service

/**
 * Abstract representation of a pull request.
 *
 * @property id ID of the PR
 * @property name Display name for the PR
 * @property link HTML link to the PR
 * @property merged Has this PR been merged?
 */
data class SCMPullRequest(
    val id: String,
    val name: String,
    val link: String,
    val merged: Boolean,
)
