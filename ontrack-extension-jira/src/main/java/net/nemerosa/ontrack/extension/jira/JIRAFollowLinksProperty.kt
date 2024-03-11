package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

/**
 * List of links to follow when displaying information about an issue.
 */
data class JIRAFollowLinksProperty(
    @APILabel("Link names")
    @APIDescription("List of links to follow when displaying information about an issue.")
    val linkNames: List<String>,
)
