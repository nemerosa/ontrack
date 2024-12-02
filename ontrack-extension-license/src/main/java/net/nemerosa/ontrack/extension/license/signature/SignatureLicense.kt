package net.nemerosa.ontrack.extension.license.signature

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseFeatureData
import java.time.LocalDate

data class SignatureLicense(
    val name: String,
    val assignee: String,
    val validUntil: LocalDate?,
    val maxProjects: Int,
    val features: List<LicenseFeatureData>,
) {
    fun toLicense(type: String) =
        License(
            type = type,
            name = name,
            assignee = assignee,
            validUntil = validUntil?.atStartOfDay()?.plusHours(23),
            active = true,
            maxProjects = maxProjects,
            features = features,
        )
}
