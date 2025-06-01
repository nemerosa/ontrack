package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.extension.license.signature.SignatureLicense
import net.nemerosa.ontrack.extension.license.signature.SignatureLicenseException
import net.nemerosa.ontrack.model.support.NameValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProductionLicenseServiceTest {

    @Test
    fun `No license content provided`() {
        val service = ProductionLicenseService(
            licenseConfigurationProperties = LicenseConfigurationProperties().apply {
                key = ""
            },
            licenseKeyPath = "/testing/keys/embedded.key"
        )
        assertFailsWith<SignatureLicenseException> {
            service.license
        }
    }

    @Test
    fun `License OK`() {
        val service = ProductionLicenseService(
            licenseConfigurationProperties = LicenseConfigurationProperties().apply {
                key =
                    "eyJkYXRhIjoiZXlKdVlXMWxJam9pV0V3aUxDSmhjM05wWjI1bFpTSTZJazVsYldWeWIzTmhJRXh2WTJGc0lpd2lkbUZzYVdSVmJuUnBiQ0k2Ym5Wc2JDd2liV0Y0VUhKdmFtVmpkSE1pT2pBc0ltWmxZWFIxY21WeklqcGJleUpwWkNJNkltVjRkR1Z1YzJsdmJpNWxiblpwY205dWJXVnVkSE1pTENKbGJtRmliR1ZrSWpwMGNuVmxMQ0prWVhSaElqcGJleUp1WVcxbElqb2liV0Y0Ulc1MmFYSnZibTFsYm5Seklpd2lkbUZzZFdVaU9pSXdJbjFkZlYxOSIsInNpZ25hdHVyZSI6Ik1FVUNJUURiNmd5WkZNbktINUxXdFhTODFtdFlwMzN2STFRSFNmNldTREdGd2UxUnBRSWdOekMwditTd29ad1pVZ01CcFlDeCtmb2g0L0EwSnVhd0hYU2V5UW1sQ3dvPSJ9"
            },
            licenseKeyPath = "/testing/keys/embedded.key"
        )
        val license = service.license
        assertEquals(
            license().toLicense("Production"),
            license
        )
        assertEquals(true, license.isFeatureEnabled("extension.environments"))
    }

    @Test
    fun `Evaluation license key`() {
        val service = ProductionLicenseService(
            licenseConfigurationProperties = LicenseConfigurationProperties().apply {
                key =
                    "eyJkYXRhIjoiZXlKdVlXMWxJam9pVXlJc0ltRnpjMmxuYm1WbElqb2lVSFZpYkdsaklpd2lkbUZzYVdSVmJuUnBiQ0k2SWpJd01qVXRNVEl0TXpFaUxDSnRZWGhRY205cVpXTjBjeUk2TVRBc0ltWmxZWFIxY21WeklqcGJleUpwWkNJNkltVjRkR1Z1YzJsdmJpNWxiblpwY205dWJXVnVkSE1pTENKbGJtRmliR1ZrSWpwbVlXeHpaU3dpWkdGMFlTSTZXM3NpYm1GdFpTSTZJbTFoZUVWdWRtbHliMjV0Wlc1MGN5SXNJblpoYkhWbElqb2lNQ0o5WFgxZExDSnRaWE56WVdkbElqb2lXVzkxSUdGeVpTQjFjMmx1WnlCaGJpQmxkbUZzZFdGMGFXOXVJR3hwWTJWdWMyVXVJbjA9Iiwic2lnbmF0dXJlIjoiTUVVQ0lFMWNjQWQxT25ZQXl2M3B4c3ZaQWc0eDE1Q3dmY3FjMFNNRm12ZUU5TVRDQWlFQTJJZHVsZEtxek5DU2Q2VHJNNGxsczhzVGlHWXQ3Nmw5bFRZQ3pFdDBKMjQ9In0="
            },
        )
        val license = service.license
        assertEquals(
            "You are using an evaluation license.",
            license.message
        )
        assertEquals(false, license.isFeatureEnabled("extension.environments"))
        assertEquals(10, license.maxProjects)
    }

    @Test
    fun `License with message OK`() {
        val service = ProductionLicenseService(
            licenseConfigurationProperties = LicenseConfigurationProperties().apply {
                key =
                    "eyJkYXRhIjoiZXlKdVlXMWxJam9pV0V3aUxDSmhjM05wWjI1bFpTSTZJazVsYldWeWIzTmhJRXh2WTJGc0lpd2lkbUZzYVdSVmJuUnBiQ0k2Ym5Wc2JDd2liV0Y0VUhKdmFtVmpkSE1pT2pBc0ltWmxZWFIxY21WeklqcGJleUpwWkNJNkltVjRkR1Z1YzJsdmJpNWxiblpwY205dWJXVnVkSE1pTENKbGJtRmliR1ZrSWpwMGNuVmxMQ0prWVhSaElqcGJleUp1WVcxbElqb2liV0Y0Ulc1MmFYSnZibTFsYm5Seklpd2lkbUZzZFdVaU9pSXdJbjFkZlYwc0ltMWxjM05oWjJVaU9pSlVhR2x6SUdseklHRWdkR1Z6ZENCc2FXTmxibk5sSW4wPSIsInNpZ25hdHVyZSI6Ik1FVUNJUURXM0pFTGJiK1paQnh1ZFRiQW5JZy9pbVFJR0lpUk5tMklOTCs5UktIMG1RSWdOOGg1STJrZ1cxR2tCZS92dnR5WUcxbnR1cDZ1bGJ5OTNtRnVYeTN5dHNRPSJ9"
            },
            licenseKeyPath = "/testing/keys/embedded.key"
        )
        val license = service.license
        assertEquals(
            license(message = "This is a test license").toLicense("Production"),
            license
        )
        assertEquals(true, license.isFeatureEnabled("extension.environments"))
    }

    private fun license(
        message: String? = null,
    ) = SignatureLicense(
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
        ),
        message = message,
    )

}