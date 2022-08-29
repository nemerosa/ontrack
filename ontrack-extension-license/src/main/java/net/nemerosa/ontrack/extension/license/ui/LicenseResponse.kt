package net.nemerosa.ontrack.extension.license.ui

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.control.LicenseControl

data class LicenseResponse(
    val license: License?,
    val licenseControl: LicenseControl,
)