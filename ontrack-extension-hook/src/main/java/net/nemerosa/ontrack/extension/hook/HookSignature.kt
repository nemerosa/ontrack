package net.nemerosa.ontrack.extension.hook

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.binary.StringUtils
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils

object HookSignature {

    fun checkSignature(
        body: String,
        signature: String,
        key: String,
        algorithms: HmacAlgorithms = HmacAlgorithms.HMAC_SHA_512,
    ) {
        if (key.isBlank()) {
            throw HookSignatureMissingTokenException()
        } else {
            val expectedSignature = Hex.encodeHexString(
                HmacUtils
                    .getInitializedMac(algorithms, StringUtils.getBytesUtf8(key))
                    .doFinal(StringUtils.getBytesUtf8(body))
            )
            if (expectedSignature != signature) {
                throw HookSignatureMismatchException()
            }
        }
    }
}