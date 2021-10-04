package net.nemerosa.ontrack.extension.github.model

import net.nemerosa.ontrack.common.Time
import java.time.LocalDateTime

/**
 * Token information
 *
 * @property token Value of the token
 * @property validUntil Validity of the token
 */
data class GHToken(
    val token: String,
    val validUntil: LocalDateTime,
) {
    fun isValid() = (Time.now() <= validUntil)
}