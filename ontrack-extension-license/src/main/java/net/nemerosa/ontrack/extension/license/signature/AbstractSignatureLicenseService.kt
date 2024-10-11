package net.nemerosa.ontrack.extension.license.signature

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

abstract class AbstractSignatureLicenseService : LicenseService {

    abstract val encodedLicense: String?
    abstract val encodedSignature: String?
    abstract val licenseType: String

    @OptIn(ExperimentalEncodingApi::class)
    override val license: License? by lazy {
        // Reads the license content (decoding with Base64)
        val licenseContent = encodedLicense
            ?.takeIf { it.isNotBlank() }
            ?.run {
                Base64.decode(this)
            }
            ?: throw SignatureLicenseException("No license content has been provided")

        // Gets the license signature
        val licenseSignatureBytes = encodedSignature
            ?.takeIf { it.isNotBlank() }
            ?.run {
                Base64.decode(this)
            }
            ?: throw SignatureLicenseException("No license signature has been provided")

        // Gets the license key
        val licenseKey = this::class.java.getResourceAsStream("/keys/embedded.key")
            ?.reader()
            ?.readText()
            ?: throw SignatureLicenseException("No license key has been provided")

        // Getting the public key
        val publicKeyPEM: String = licenseKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")
        val decoded: ByteArray = Base64.decode(publicKeyPEM)
        val keyFactory: KeyFactory = KeyFactory.getInstance("EC")
        val spec = X509EncodedKeySpec(decoded)
        val publicKey = keyFactory.generatePublic(spec)

        // Checking the signature of the encoded license content
        val signature: Signature = Signature.getInstance("SHA256withECDSA")
        signature.initVerify(publicKey)
        signature.update(licenseContent)
        val signatureOK = signature.verify(licenseSignatureBytes)
        if (!signatureOK) {
            throw SignatureLicenseException("License signature verification failed")
        }

        // Decoding the license content (base64)
        val decodedLicense = licenseContent.toString(Charsets.UTF_8)

        // Parsing the license
        val signatureLicense = decodedLicense.parseAsJson().parse<SignatureLicense>()

        // OK
        signatureLicense.toLicense(licenseType)
    }

}