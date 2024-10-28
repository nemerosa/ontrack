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
                key = null
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
                key =
                    "eyJkYXRhIjoiZXlKdVlXMWxJam9pVUhKbGJXbDFiU0lzSW1GemMybG5ibVZsSWpvaVRtVnRaWEp2YzJFaUxDSjJZV3hwWkZWdWRHbHNJanB1ZFd4c0xDSnRZWGhRY205cVpXTjBjeUk2TUN3aVptVmhkSFZ5WlhNaU9sc2laWGgwWlc1emFXOXVMbVZ1ZG1seWIyNXRaVzUwY3lKZGZRPT0iLCJzaWduYXR1cmUiOiJNRVlDSVFEeUxkUFhBL1k0RjdzR0V1V3RUN3laa0gzQnVMMWZ6S0hRV3hnRDJVSm12UUloQU1seXFxblZkdXpPcEx6VVVyV1N5anNqNU1LVE9WYlRSdTFCam9jZXlqRWUifQ=="
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
        name = "Premium",
        assignee = "Nemerosa",
        validUntil = null,
        maxProjects = 0,
        features = listOf(
            "extension.environments",
        )
    )

}