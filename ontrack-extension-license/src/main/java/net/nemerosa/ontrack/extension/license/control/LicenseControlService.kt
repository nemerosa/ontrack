package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicensedFeature

interface LicenseControlService {

    fun control(license: License?): LicenseControl

    fun getLicensedFeatures(license: License): List<LicensedFeature>

    /**
     * Checks if the given feature is enabled for the current license.
     */
    fun isFeatureEnabled(featureID: String): Boolean

}