package net.nemerosa.ontrack.model.utils

import kotlinx.coroutines.*
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.coroutines.CoroutineContext

/**
 * Launches some code as a coroutine while preserving the Spring security context.
 *
 * @param context Context where to execute the coroutine. Using the [IO context][Dispatchers.IO] by default,
 * whose number of threads can be configured (see docs).
 * @param code Code to run asynchronously
 * @return Coroutine job which can be used to check the completion
 */
fun launchWithSecurityContext(
    context: CoroutineContext = Dispatchers.IO,
    code: suspend CoroutineScope.() -> Unit,
): Job {
    val securityContext = SecurityContextHolder.getContext()
    return try {
        CoroutineScope(context).launch {
            SecurityContextHolder.setContext(securityContext)
            code()
        }
    } finally {
        SecurityContextHolder.setContext(securityContext)
    }
}

fun <T> launchAsyncWithSecurityContext(
    context: CoroutineContext = Dispatchers.IO,
    code: suspend CoroutineScope.() -> T,
): Deferred<T> {
    val securityContext = SecurityContextHolder.getContext()
    return try {
        CoroutineScope(context).async {
            SecurityContextHolder.setContext(securityContext)
            code()
        }
    } finally {
        SecurityContextHolder.setContext(securityContext)
    }
}