package net.nemerosa.ontrack.extension.license.control

data class LicenseControl(
    val expired: Boolean,
    val projectCountExceeded: Boolean,
) {

    val valid = !expired && !projectCountExceeded

    companion object {
        val OK = LicenseControl(
            expired = false,
            projectCountExceeded = false,
        )
    }
}
