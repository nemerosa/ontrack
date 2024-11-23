package net.nemerosa.ontrack.kdsl.connector

data class FileContent(
    val name: String,
    val content: ByteArray,
    val type: String,
)