package net.nemerosa.ontrack.extension.tfc.hook

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.binary.StringUtils
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils

object TFCHookSignature {

    fun checkPayloadSignature(
        body: String,
        signature: String,
        token: String
    ): TFCHookSignatureCheck {
        val expectedSignature = Hex.encodeHexString(
            HmacUtils
                .getInitializedMac(HmacAlgorithms.HMAC_SHA_512, StringUtils.getBytesUtf8(token))
                .doFinal(StringUtils.getBytesUtf8(body))
        )
        return if (expectedSignature != signature) {
            TFCHookSignatureCheck.MISMATCH
        } else {
            TFCHookSignatureCheck.OK
        }
    }

}