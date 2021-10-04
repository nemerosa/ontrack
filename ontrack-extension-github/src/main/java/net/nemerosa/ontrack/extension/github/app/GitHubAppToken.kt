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
    fun isValid() = (Time.now() <= validUntil)
}