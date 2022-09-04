package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseConfigurationProperties
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LicenseControlServiceImpl(
    private val licenseConfigurationProperties: LicenseConfigurationProperties,
    private val structureService: StructureService,
    private val securityService: SecurityService,
) : LicenseControlService {

    override fun control(license: License?): LicenseControl =
        if (license != null) {
            val count = securityService.asAdmin {
                structureService.projectList.size
            }
            control(license, count)
        } else {
            LicenseControl.OK
        }


    fun control(license: License, count: Int) = LicenseControl(
        active = license.active,
        expiration = getExpiration(license),
        projectCountExceeded = isProjectCountExceeded(license, count),
    )

    private fun getExpiration(license: License): LicenseExpiration =
        when {
            isExpired(license) -> LicenseExpiration.EXPIRED
            isAlmostExpired(license) -> LicenseExpiration.ALMOST
            else -> LicenseExpiration.OK
        }

    private fun isExpired(license: License): Boolean =
        if (license.validUntil != null) {
            val now = Time.now()
            now >= license.validUntil
        } else {
            false
        }

    internal fun isAlmostExpired(license: License) =
        if (license.validUntil != null) {
            val now = Time.now()
            val validUntil = license.validUntil
            val warningTime = validUntil.minus(licenseConfigurationProperties.warning)
            warningTime <= now && now < validUntil
        } else {
            false
        }

    private fun isProjectCountExceeded(license: License, count: Int): Boolean =
        license.maxProjects in 1..count

}