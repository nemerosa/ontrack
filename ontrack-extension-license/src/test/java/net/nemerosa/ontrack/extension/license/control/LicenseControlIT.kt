package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.license.AbstractLicenseTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class LicenseControlIT : AbstractLicenseTestSupport() {

    @Test
    fun `Cannot create a project when license is expired`() {
        withLicense(validUntil = Time.now().minusDays(1)) {
            assertFailsWith<LicenseExpiredException> {
                project()
            }
        }
    }

    @Test
    fun `Cannot create a project when number of projects is exceeded`() {
        repeat(2) {
            project()
        }
        asAdmin {
            val count = structureService.projectList.size
            withLicense(maxProjects = count - 1) {
                assertFailsWith<LicenseMaxProjectExceededException> {
                    project()
                }
            }
        }
    }

}