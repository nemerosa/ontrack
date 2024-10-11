package net.nemerosa.ontrack.extension.license.embedded

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.license.signature.SignatureLicense
import net.nemerosa.ontrack.extension.license.signature.SignatureLicenseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import org.junit.jupiter.api.Test
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignatureLicenseServiceTest {

    @Test
    fun `No license content provided`() {
        val service = EmbeddedLicenseService(
            EmbeddedLicenseConfigurationProperties().apply {
                license = null
            }
        )
        assertFailsWith<SignatureLicenseException> {
            service.license
        }
    }

    @Test
    fun `No license signature provided`() {
        val service = EmbeddedLicenseService(
            EmbeddedLicenseConfigurationProperties().apply {
                license = encodedLicense()
                signature = null
            }
        )
        assertFailsWith<SignatureLicenseException> {
            service.license
        }
    }

    @Test
    fun `License OK`() {
        val service = EmbeddedLicenseService(
            EmbeddedLicenseConfigurationProperties().apply {
                license =
                    "ewogICJuYW1lIjogIk5lbWVyb3NhIiwKICAiYXNzaWduZWUiOiAiRGFtaWVuIiwKICAidmFsaWRVbnRpbCI6IG51bGwsCiAgIm1heFByb2plY3RzIjogMCwKICAiZmVhdHVyZXMiOiBbCiAgICAiZXh0ZW5zaW9uLmVudmlyb25tZW50cyIKICBdCn0KCg=="
                signature =
                    "MEUCIQD/ADUHavkC+yUOQhsj6O8NOHR10xp7d6vl2HuG2qo1LgIgHPD6fsQPd6gmQgjxkGLhQu1Hs+HNuHQ6MFDzl68YFNQ="
            }
        )
        assertEquals(
            license().toLicense("Embedded"),
            service.license
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun encodedLicense(): String =
        Base64.encode(
            jsonLicense().format().toByteArray(Charsets.UTF_8),
        )

    private fun jsonLicense(): JsonNode =
        license().asJson()

    private fun license() = SignatureLicense(
        name = "Nemerosa",
        assignee = "Damien",
        validUntil = null,
        maxProjects = 0,
        features = listOf(
            "extension.environments",
        )
    )

}