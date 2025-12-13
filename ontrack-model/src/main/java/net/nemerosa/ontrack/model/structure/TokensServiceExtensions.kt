package net.nemerosa.ontrack.model.structure

import org.springframework.security.access.AccessDeniedException

fun TokensService.checkTokenForSecurityContext(
    token: String,
    message: String,
) {
    if (!useTokenForSecurityContext(token)) {
        throw AccessDeniedException(message)
    }
}
