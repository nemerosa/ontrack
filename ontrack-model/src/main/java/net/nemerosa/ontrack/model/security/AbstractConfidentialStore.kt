package net.nemerosa.ontrack.model.security

import java.security.SecureRandom

/**
 * Provides random bytes.
 */
abstract class AbstractConfidentialStore : ConfidentialStore {
    private val sr = SecureRandom()

    final override fun randomBytes(size: Int): ByteArray {
        val random = ByteArray(size)
        sr.nextBytes(random)
        return random
    }

}