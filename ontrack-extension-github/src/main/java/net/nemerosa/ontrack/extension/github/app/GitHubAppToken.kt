package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.common.Time
import java.time.LocalDateTime

/**
 * Token information
 *
 * @property token Value of the token
 * @property validUntil Validity of the token
 */
data class GitHubAppToken(
    val token: String,
    val validUntil: LocalDateTime,
) {

    private var invalid: Boolean = false

    fun invalidate() {
        invalid = true
    }

    fun isValid() = !invalid && (Time.now() <= validUntil)
}