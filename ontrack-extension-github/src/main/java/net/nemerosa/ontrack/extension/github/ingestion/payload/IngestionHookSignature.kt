package net.nemerosa.ontrack.extension.github.ingestion.payload

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.binary.StringUtils
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils

object IngestionHookSignature {

    fun checkPayloadSignature(body: String, signature: String, token: String) {
        val expectedSignature = Hex.encodeHexString(
            HmacUtils
                .getInitializedMac(HmacAlgorithms.HMAC_SHA_256, StringUtils.getBytesUtf8(token))
                .doFinal(StringUtils.getBytesUtf8(body))
        )
        val completeSignature = "sha256=$expectedSignature"
        if (completeSignature != signature) {
            throw GitHubIngestionHookSignatureMismatchException()
        }
    }

}