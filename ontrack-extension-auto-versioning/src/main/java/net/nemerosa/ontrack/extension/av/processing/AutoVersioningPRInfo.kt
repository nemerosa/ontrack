package net.nemerosa.ontrack.extension.av.processing

/**
 * Information needed for the generation of a PR.
 *
 * @property title PR title
 * @property body PR body
 */
data class AutoVersioningPRInfo(
    val title: String,
    val body: String,
)
