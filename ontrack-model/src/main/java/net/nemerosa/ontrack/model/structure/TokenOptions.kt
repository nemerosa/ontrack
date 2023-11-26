package net.nemerosa.ontrack.model.structure

import java.time.Duration

data class TokenOptions(
    val name: String,
    val scope: TokenScope = TokenScope.USER,
    val validity: Duration? = null,
    val forceUnlimited: Boolean = false,
)