package net.nemerosa.ontrack.extension.github.ingestion.payload

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DefaultIngestionHookSignatureServiceTest {

    private lateinit var service: IngestionHookSignatureService
    private lateinit var cachedSettingsService: CachedSettingsService

    @Before
    fun before() {
        cachedSettingsService = mockk()
        service = DefaultIngestionHookSignatureService(cachedSettingsService)
    }

    @Test
    fun `Signature OK`() {
        mockToken(IngestionHookFixtures.signatureTestToken)
        assertEquals(
            IngestionHookSignatureCheckResult.OK,
            service.checkPayloadSignature(
                body = IngestionHookFixtures.signatureTestBody,
                signature = IngestionHookFixtures.signatureTestSignature,
            )
        )
    }

    @Test
    fun `Signature mismatch`() {
        mockToken("wrong-token")
        assertEquals(
            IngestionHookSignatureCheckResult.MISMATCH,
            service.checkPayloadSignature(
                body = IngestionHookFixtures.signatureTestBody,
                signature = IngestionHookFixtures.signatureTestSignature,
            )
        )
    }

    @Test
    fun `Signature token missing`() {
        mockToken("")
        assertEquals(
            IngestionHookSignatureCheckResult.MISSING_TOKEN,
            service.checkPayloadSignature(
                body = IngestionHookFixtures.signatureTestBody,
                signature = IngestionHookFixtures.signatureTestSignature,
            )
        )
    }

    private fun mockToken(token: String) {
        every { cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java) } returns GitHubIngestionSettings(
            token = token,
            retentionDays = 30,
            orgProjectPrefix = false,
        )
    }

}