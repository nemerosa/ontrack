package net.nemerosa.ontrack.extension.tfc.hook

import org.junit.Test
import kotlin.test.assertEquals

class TFCHookSignatureTest {

    @Test
    fun `Checking a signature`() {
        assertEquals(
            TFCHookSignatureCheck.OK,
            TFCHookSignature.checkPayloadSignature(
                body = TFCHookFixtures.signatureTestBody,
                signature = TFCHookFixtures.signatureTestSignature,
                token = TFCHookFixtures.signatureTestToken,
            )
        )
    }

    @Test
    fun `Checking a signature with wrong value`() {
        assertEquals(
            TFCHookSignatureCheck.MISMATCH,
            TFCHookSignature.checkPayloadSignature(
                body = TFCHookFixtures.signatureTestBody,
                signature = TFCHookFixtures.signatureTestSignature + "1",
                token = TFCHookFixtures.signatureTestToken,
            )
        )
    }

    @Test
    fun `Checking a signature with wrong token`() {
        assertEquals(
            TFCHookSignatureCheck.MISMATCH,
            TFCHookSignature.checkPayloadSignature(
                body = TFCHookFixtures.signatureTestBody,
                signature = TFCHookFixtures.signatureTestSignature + "1",
                token = TFCHookFixtures.signatureTestToken + "1",
            )
        )
    }

}