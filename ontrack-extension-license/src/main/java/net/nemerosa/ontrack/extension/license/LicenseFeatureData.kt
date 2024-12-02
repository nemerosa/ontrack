package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.model.support.NameValue

data class LicenseFeatureData(
    val id: String,
    val enabled: Boolean,
    val data: List<NameValue>,
)
