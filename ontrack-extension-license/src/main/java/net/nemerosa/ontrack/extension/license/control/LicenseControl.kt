package net.nemerosa.ontrack.extension.license.control

data class LicenseControl(
    val active: Boolean,
    val expired: Boolean,
    val projectCountExceeded: Boolean,
) {

    val valid = active && !expired && !projectCountExceeded

    companion object {
        val OK = LicenseControl(
            active = true,
            expired = false,
            projectCountExceeded = false,
        )
    }
}
