package net.nemerosa.ontrack.model.links

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription

/**
 * Decoration for the edge between two nodes on a branch links graph.
 *
 * @property feature Feature contributing to this decoration
 * @property id ID of the decoration provider
 * @property text Short text to display at edge level for the decoration
 * @property description Optional longer text, typically used as a tooltip
 * @property icon Optional name to an image for the decoration. This name is used together with the [feature] and the [id] to build a URL on the client side
 * @property url Optional link from the decoration to another page
 */
@APIDescription("Decoration for the edge between two nodes on a branch links graph.")
class BranchLinksDecoration(
    @APIDescription("Feature contributing to this decoration")
    val feature: ExtensionFeatureDescription,
    @APIDescription("ID of the decoration provider")
    val id: String,
    @APIDescription("Short text to display at edge level for the decoration")
    val text: String,
    @APIDescription("Optional longer text, typically used as a tooltip")
    val description: String? = null,
    @APIDescription("Optional name to an image for the decoration. This name is used together with the [feature] and the [id] to build a URL on the client side")
    val icon: String? = null,
    @APIDescription("Optional link from the decoration to another page")
    val url: String? = null
)