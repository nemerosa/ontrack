package net.nemerosa.ontrack.extension.license.control

inline fun <reified T : Any> LicenseControlService.parseLicenseData(featureID: String): T? =
    parseLicenseDataInto(featureID, T::class)
