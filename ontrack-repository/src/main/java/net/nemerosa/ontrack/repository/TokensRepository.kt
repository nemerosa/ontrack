package net.nemerosa.ontrack.repository

import java.time.LocalDateTime

/**
 * Access to the `TOKENS` table.
 */
interface TokensRepository {

    /**
     * Invalidates any token for the account designated by its [id].
     */
    fun invalidate(id: Int)

    /**
     * Saves a token.
     *
     * @param id ID of the account
     * @param encodedToken Token to save (encoded)
     * @param time Creation time
     */
    fun save(id: Int, encodedToken: String, time: LocalDateTime)

}