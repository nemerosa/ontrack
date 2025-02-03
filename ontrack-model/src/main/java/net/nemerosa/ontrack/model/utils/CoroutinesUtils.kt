package net.nemerosa.ontrack.model.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
            try {
                code()
            } finally {
                SecurityContextHolder.clearContext()
            }
        }
    } finally {
        SecurityContextHolder.setContext(securityContext)
    }
}