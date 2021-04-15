package net.nemerosa.ontrack.model.links

/**
 * Decoration for the edge between two nodes on a branch links graph.
 *
 * @property id ID of the decoration provider
 * @property text Short text to display at edge level for the decoration
 * @property description Optional longer text, typically used as a tooltip
 * @property iconUrl Optional link to an image for the decoration
 * @property url Optional link from the decoration to another page
 */
class BranchLinksDecoration(
    val id: String,
    val text: String,
    val description: String?,
    val iconUrl: String?,
    val url: String?
)