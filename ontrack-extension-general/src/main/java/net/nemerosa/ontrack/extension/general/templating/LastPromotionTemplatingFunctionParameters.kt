package net.nemerosa.ontrack.extension.general.templating

import net.nemerosa.ontrack.model.annotations.APIDescription

data class LastPromotionTemplatingFunctionParameters(
    @APIDescription("Project where to look for the build")
    val project: String,
    @APIDescription("Restricting the search to this branch")
    val branch: String? = null,
    @APIDescription("Name of the promotion level to look for")
    val promotion: String,
    @APIDescription("Using the release name or build name. `auto` for the first available, `release` for a required release name, `name` for only the name")
    val name: String? = null,
    @APIDescription("Renders a link to the build or only the name")
    val link: Boolean? = null,
)

