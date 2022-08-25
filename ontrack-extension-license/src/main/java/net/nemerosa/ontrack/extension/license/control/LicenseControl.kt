package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.license.License

object LicenseControl {

    fun control(license: License, count: Int) {
        controlValidity(license)
        controlProjects(license, count)
    }

    private fun controlValidity(license: License) {
        if (license.validUntil != null) {
            val time = Time.now()
            if (time > license.validUntil) {
                throw LicenseExpiredException(license)
            }
        }
    }

    private fun controlProjects(license: License, count: Int) {
        if (license.maxProjects in 1..count) {
            throw LicenseMaxProjectExceededException(license)
        }
    }

}