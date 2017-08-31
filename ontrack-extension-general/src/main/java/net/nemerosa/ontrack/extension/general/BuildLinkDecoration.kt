package net.nemerosa.ontrack.extension.general

import java.net.URI

class BuildLinkDecoration(
        val project: String,
        val build: String,
        val uri: URI,
        val promotions: List<BuildLinkDecorationPromotion>
)
