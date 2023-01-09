package net.nemerosa.ontrack.model.security

import org.springframework.security.core.context.SecurityContextHolder
import java.util.concurrent.CompletableFuture

fun <S, T> CompletableFuture<S>.thenSecureApply(
        code: (S) -> T
): CompletableFuture<T> {
    val context = SecurityContextHolder.getContext()
    return thenApply { input ->
        val old = SecurityContextHolder.getContext()
        try {
            SecurityContextHolder.setContext(context)
            code(input)
        } finally {
            SecurityContextHolder.setContext(old)
        }
    }
}