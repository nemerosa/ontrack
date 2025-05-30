package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.license.*
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseInto
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Service
@Transactional
class LicenseControlServiceImpl(
    private val licenseConfigurationProperties: LicenseConfigurationProperties,
    private val structureService: StructureService,
    private val securityService: SecurityService,
    private val licenseService: LicenseService,
    private val licensedFeatureProviders: List<LicensedFeatureProvider>,
) : LicenseControlService {

    override fun control(license: License): LicenseControl {
        val count = securityService.asAdmin {
            structureService.projectList.size
        }
        return control(license, count)
    }

    override fun getLicensedFeatures(license: License): List<LicensedFeature> =
        licensedFeatureProviders.flatMap {
            it.providedFeatures
        }.map {
            LicensedFeature(
                id = it.id,
                name = it.name,
                enabled = it.alwaysEnabled || license.isFeatureEnabled(it.id),
                data = license.findFeatureData(it.id)?.data ?: emptyList(),
            )
        }.sortedBy { it.name }

    override fun isFeatureEnabled(featureID: String): Boolean {
        val license = licenseService.license
        return license.active && license.isFeatureEnabled(featureID)
    }

    override fun <T : Any> parseLicenseDataInto(featureID: String, type: KClass<T>): T? {
        val license = licenseService.license
        val feature = license.findFeatureData(featureID)
        return feature?.data?.associate { it.name to it.value }?.asJson()?.parseInto(type)
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