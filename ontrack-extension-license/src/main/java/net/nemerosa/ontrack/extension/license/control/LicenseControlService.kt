package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.extension.license.License

interface LicenseControlService {

    fun control(license: License?): LicenseControl

}