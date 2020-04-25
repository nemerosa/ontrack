package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.Time
import java.time.Duration
import java.time.LocalDateTime

/**
 * Representation of a user token
 *
 * @param value Token value
 * @param creation Creation timestamp
 * @param validUntil Indicates until when the token is valid - `null` if forever valid
 */
data class Token(
        val value: String,
        val creation: LocalDateTime,
        val validUntil: LocalDateTime?
) {

    /**
     * Returns an obfuscated version of this token.
     */
    fun obfuscate() = Token("", creation, validUntil)

    /**
     * Returns a new token with same [value] and [creation]
     * but with [validUntil] computed from [creation] according
     * to the given [validity] period. If the [validity] is `null`, [zero][Duration.isZero] or [negative][Duration.isNegative],
     * the validity end date is set to `null`, meaning that the token never expires.
     */
    fun validFor(validity: Duration?): Token =
            if (validity == null || validity.isZero || validity.isNegative) {
                Token(
                        value,
                        creation,
                        null
                )
            } else {
                Token(
                        value,
                        creation,
                        creation + validity
                )
            }

    /**
     * Checks if this token is valid
     */
    fun isValid(time: LocalDateTime = Time.now()) =
            validUntil == null || validUntil >= time
}