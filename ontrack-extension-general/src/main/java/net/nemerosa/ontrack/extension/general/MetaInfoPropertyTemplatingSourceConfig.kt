package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.annotations.APIDescription

data class MetaInfoPropertyTemplatingSourceConfig(
    @APIDescription("Name of the key of the meta information to get")
    val name: String,
    @APIDescription("Category of the key of the meta information to get")
    val category: String? = null,
    @APIDescription("If true, an error is raised when meta information is not found")
    val error: Boolean = false,
    @APIDescription("If true, the link of the meta information is rendered instead of the value")
    val link: Boolean = false,
)