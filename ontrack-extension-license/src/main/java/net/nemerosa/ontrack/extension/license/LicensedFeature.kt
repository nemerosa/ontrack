package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.model.support.NameValue

data class LicensedFeature(
    val id: String,
    val name: String,
    val enabled: Boolean,
    val data: List<NameValue>,
)
