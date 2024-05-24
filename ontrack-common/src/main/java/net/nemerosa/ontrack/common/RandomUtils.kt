package net.nemerosa.ontrack.common

import kotlin.random.Random

/**
 * Generates a random string using the provided [Random] receiver.
 *
 * @param length Length of the string to generate
 * @param random Optional [Random] object to the sequence
 * @return Generated string
 */
fun generateRandomString(length: Int, random: Random = Random): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { charset[random.nextInt(charset.length)] }
        .joinToString("")
}
