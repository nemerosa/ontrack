package net.nemerosa.ontrack.extension.support.client

class ClientConnection
@JvmOverloads
constructor(
        val url: String,
        val user: String?,
        val password: String?,
        val timeoutSeconds: Int? = 0
)
