package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Part of the BuildSearchForm that delegates to some extension.")
data class BuildSearchFormExtension(
    @APIDescription("ID of the extension")
    val extension: String,
    @APIDescription("Search token for the extension")
    val value: String,
)