package net.nemerosa.ontrack.common

import kotlin.random.Random

fun generateRandomString(length: Int): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { charset[Random.nextInt(charset.length)] }
        .joinToString("")
}
