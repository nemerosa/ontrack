package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppInstallation
import java.time.LocalDateTime

/**
 * Token information
 *
 * @property token Value of the token
 * @property createdAt Date/time of the creation of this token
 * @property validUntil Validity of the token
 * @property installation Installation of the app
 */
data class GitHubAppToken(
    val token: String,
    val createdAt: LocalDateTime,
    val validUntil: LocalDateTime,
    val installation: GitHubAppInstallation,
) {

    private var invalid: Boolean = false

    fun invalidate() {
        invalid = true
    }

    fun isValid() = !invalid && (Time.now() <= validUntil)
}