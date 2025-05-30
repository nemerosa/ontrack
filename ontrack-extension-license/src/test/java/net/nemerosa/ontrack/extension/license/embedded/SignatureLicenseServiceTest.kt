package net.nemerosa.ontrack.extension.license.embedded

import net.nemerosa.ontrack.extension.license.LicenseFeatureData
import net.nemerosa.ontrack.extension.license.signature.SignatureLicense
import net.nemerosa.ontrack.extension.license.signature.SignatureLicenseException
import net.nemerosa.ontrack.model.support.NameValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignatureLicenseServiceTest {

    @Test
    fun `No license content provided`() {
        val service = EmbeddedLicenseService(
            embeddedLicenseConfigurationProperties = EmbeddedLicenseConfigurationProperties().apply {
                key = null
            },
            licenseKeyPath = "/testing/keys/embedded.key"
        )
        assertFailsWith<SignatureLicenseException> {
            service.license
        }
    }

    @Test
    fun `License OK`() {
        val service = EmbeddedLicenseService(
            embeddedLicenseConfigurationProperties = EmbeddedLicenseConfigurationProperties().apply {
                key =
                    "eyJkYXRhIjoiZXlKdVlXMWxJam9pV0V3aUxDSmhjM05wWjI1bFpTSTZJazVsYldWeWIzTmhJRXh2WTJGc0lpd2lkbUZzYVdSVmJuUnBiQ0k2Ym5Wc2JDd2liV0Y0VUhKdmFtVmpkSE1pT2pBc0ltWmxZWFIxY21WeklqcGJleUpwWkNJNkltVjRkR1Z1YzJsdmJpNWxiblpwY205dWJXVnVkSE1pTENKbGJtRmliR1ZrSWpwMGNuVmxMQ0prWVhSaElqcGJleUp1WVcxbElqb2liV0Y0Ulc1MmFYSnZibTFsYm5Seklpd2lkbUZzZFdVaU9pSXdJbjFkZlYxOSIsInNpZ25hdHVyZSI6Ik1FVUNJUURiNmd5WkZNbktINUxXdFhTODFtdFlwMzN2STFRSFNmNldTREdGd2UxUnBRSWdOekMwditTd29ad1pVZ01CcFlDeCtmb2g0L0EwSnVhd0hYU2V5UW1sQ3dvPSJ9"
            },
            licenseKeyPath = "/testing/keys/embedded.key"
        )
        val license = service.license
        assertEquals(
            license().toLicense("Embedded"),
            license
        )
        assertEquals(true, license.isFeatureEnabled("extension.environments"))
    }

    private fun license() = SignatureLicense(
        name = "XL",
        assignee = "Nemerosa Local",
        validUntil = null,
        maxProjects = 0,
        features = listOf(
            LicenseFeatureData(
                id = "extension.environments",
                enabled = true,
                data = listOf(
                    NameValue("maxEnvironments", "0")
                ),
            )
        )
    )

}