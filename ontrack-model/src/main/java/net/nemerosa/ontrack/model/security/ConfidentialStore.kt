package net.nemerosa.ontrack.model.security

interface ConfidentialStore {
    /**
     * Persists the payload of key to a persisted storage (such as disk.)
     * The expectation is that the persisted form is secure.
     */
    fun store(key: String, payload: ByteArray)

    /**
     * Reverse operation of [store]
     *
     * @return null the data has not been previously persisted, or if the data was tampered.
     */
    fun load(key: String): ByteArray?

    /**
     * Works like [java.security.SecureRandom.nextBytes].
     *
     * This enables implementations to consult other entropy sources, if it's available.
     */
    fun randomBytes(size: Int): ByteArray
}