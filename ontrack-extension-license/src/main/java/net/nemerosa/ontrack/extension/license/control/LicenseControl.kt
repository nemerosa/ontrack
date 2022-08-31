package net.nemerosa.ontrack.extension.license.control

data class LicenseControl(
    val active: Boolean,
    val expiration: LicenseExpiration,
    val projectCountExceeded: Boolean,
) {

    val valid = active && (expiration != LicenseExpiration.EXPIRED) && !projectCountExceeded

    companion object {
        val OK = LicenseControl(
            active = true,
            expiration = LicenseExpiration.OK,
            projectCountExceeded = false,
        )
    }
}
