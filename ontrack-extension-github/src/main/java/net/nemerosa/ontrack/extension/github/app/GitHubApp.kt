package net.nemerosa.ontrack.extension.github.app

import io.jsonwebtoken.Jwts
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppClient
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppInstallation
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class GitHubApp(
    private val gitHubAppClient: GitHubAppClient
) {

    companion object {
        /**
         * Generates a JWT suitable for calling the GitHub App API.
         */
        fun generateJWT(appId: String, appPrivateKey: String): String {
            val now = LocalDateTime.now()
            // issued at current time, 60 seconds in the past to allow for clock drift
            val issuedAt = Date.from(now.minusSeconds(60).atZone(ZoneId.systemDefault()).toInstant())
            // JWT expiration time (10 minute maximum)
            val expiresAt = Date.from(now.plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant())

            // Encoding the key for JWT
            val key = readPrivateKey(appPrivateKey)

            // Encoding the JWT
            return Jwts.builder()
                .setIssuer(appId)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(key)
                .compact()
        }

        fun readPrivateKey(key: String): RSAPrivateKey {
            if (" RSA " in key) {
                throw InvalidKeySpecException("Private key must be a PKCS#8 formatted string, to convert it from PKCS#1 use: openssl pkcs8 -topk8 -inform PEM -outform PEM -in current-key.pem -out new-key.pem -nocrypt")
            } else {
                val privateKeyPEM = key
                    .replace("-----[^-]+-----".toRegex(), "")
                    .replace("\\s".toRegex(), "")
                val decoded: ByteArray = Base64.getDecoder().decode(privateKeyPEM)
                val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
                val keySpec = PKCS8EncodedKeySpec(decoded)
                return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
            }
        }
    }

    /**
     * Gets the installation to use for the client.
     *
     * @param jwt JWT generated by the [generateJWT] method
     * @param appId ID of the GitHub App
     * @param appInstallationAccountName Optional account name of the installation, used to differentiate app installations when need be
     * @return App installation to use
     */
    fun getInstallation(jwt: String, appId: String, appInstallationAccountName: String?): GitHubAppInstallation {
        // Gets the list of installations for the app
        val installations = gitHubAppClient.getAppInstallations(jwt)
        // If no installation
        return if (installations.isEmpty()) {
            throw GitHubAppNoInstallationException(appId)
        }
        // Only one installation
        else if (installations.size == 1) {
            val installation = installations.first()
            if (appInstallationAccountName != null && installation.account.login != appInstallationAccountName) {
                throw GitHubAppNoInstallationForAccountException(appId, appInstallationAccountName)
            } else {
                installation
            }
        }
        // Several installations
        else {
            if (appInstallationAccountName != null) {
                val installation = installations.find {
                    it.account.login == appInstallationAccountName
                }
                installation ?: throw GitHubAppNoInstallationForAccountException(appId, appInstallationAccountName)
            } else {
                throw GitHubAppSeveralInstallationsException(appId)
            }
        }
    }

    /**
     * Gets a new token for an app installation
     *
     * @param jwt JWT generated by the [generateJWT] method
     * @param appInstallation GitHub App installation
     * @return Token & its expiration date
     */
    fun generateInstallationToken(jwt: String, appInstallation: GitHubAppInstallation): GitHubAppToken =
        gitHubAppClient.generateInstallationToken(jwt, appInstallation.id).run {
            GitHubAppToken(
                token = token,
                createdAt = Time.now(),
                installation = appInstallation,
                validUntil = Time.from(expiresAt, null) ?: error("Cannot convert the GH App token expiration date")
            )
        }

}