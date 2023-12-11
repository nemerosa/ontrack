package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.Time
import java.time.Duration
import java.time.LocalDateTime

/**
 * Representation of a user token
 *
 * @param name Token name
 * @param value Token value
 * @param creation Creation timestamp
 * @param scope Scope of this token
 * @param validUntil Indicates until when the token is valid - `null` if forever valid
 * @param lastUsed Indicates when the token was used for the last time
 */
data class Token(
    val name: String,
    val value: String,
    val creation: LocalDateTime,
    val scope: TokenScope,
    val validUntil: LocalDateTime?,
    val lastUsed: LocalDateTime?,
) {

    /**
     * Returns an obfuscated version of this token.
     */
    fun obfuscate() = Token(name, "", creation, scope, validUntil, lastUsed)

    /**
     * Returns a new token with same [value] and [creation]
     * but with [validUntil] computed from [creation] according
     * to the given [validity] period. If the [validity] is `null`, [zero][Duration.isZero] or [negative][Duration.isNegative],
     * the validity end date is set to `null`, meaning that the token never expires.
     *
     * If the scope of the token is transient, the validity period cannot be changed.
     */
    fun validFor(validity: Duration?): Token =
        if (scope.transient) {
            this
        } else if (validity == null || validity.isZero || validity.isNegative) {
            Token(
                name,
                value,
                creation,
                scope,
                null,
                lastUsed,
            )
        } else {
            Token(
                name,
                value,
                creation,
                scope,
                creation + validity,
                lastUsed,
            )
        }

    /**
     * Checks if this token is valid
     */
    fun isValid(time: LocalDateTime = Time.now()) =
        validUntil == null || validUntil >= time

    /**
     * Validity now
     */
    val valid: Boolean get() = isValid()

    /**
     * Setting the last used date
     */
    fun withLastUsed(lastUsed: LocalDateTime) = Token(
        name = name,
        value = value,
        creation = creation,
        scope = scope,
        validUntil = validUntil,
        lastUsed = lastUsed,
    )
}