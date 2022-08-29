package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LicenseControlServiceImpl(
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
        expired = isExpired(license),
        projectCountExceeded = isProjectCountExceeded(license, count),
    )

    private fun isExpired(license: License): Boolean =
        if (license.validUntil != null) {
            val now = Time.now()
            now >= license.validUntil
        } else {
            false
        }

    private fun isProjectCountExceeded(license: License, count: Int): Boolean =
        count >= license.maxProjects

}