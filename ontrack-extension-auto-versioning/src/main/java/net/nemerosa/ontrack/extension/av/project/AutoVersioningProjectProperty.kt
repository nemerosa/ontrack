package net.nemerosa.ontrack.extension.av.project

import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.LocalDateTime

/**
 * Stored at project level, defines some rules for the auto-versioning requests.
 *
 * @property branchIncludes List of regular expressions. AV requests match if at least one regular expression is matched by the target branch name. If empty, all target branches match (the default).
 * @property branchExcludes List of regular expressions. AV requests match if no regular expression is matched by the target branch name. If empty, the target branch is considered matching.
 * @property lastActivityDate If defined, any target branch whose last activity (last build creation) is before this date will be ignored by the auto-versioning
 */
data class AutoVersioningProjectProperty(
    @APIDescription("List of regular expressions. AV requests match if at least one regular expression is matched by the target branch name. If empty, all target branches match (the default).")
    val branchIncludes: List<String>?,
    @APIDescription("List of regular expressions. AV requests match if no regular expression is matched by the target branch name. If empty, the target branch is considered matching.")
    val branchExcludes: List<String>?,
    @APIDescription("If defined, any target branch whose last activity (last build creation) is before this date will be ignored by the auto-versioning")
    val lastActivityDate: LocalDateTime?,
)
