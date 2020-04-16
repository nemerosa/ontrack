package net.nemerosa.ontrack.service.security

import javax.crypto.Cipher

interface ConfidentialKey {
    /**
     * Name of the key. This is used as the file name.
     */
    val id: String

    fun encrypt(): Cipher
    fun decrypt(): Cipher
    fun encrypt(plain: String): String
    fun decrypt(crypted: String): String

    fun exportKey(): String?

    fun importKey(key: String)
}