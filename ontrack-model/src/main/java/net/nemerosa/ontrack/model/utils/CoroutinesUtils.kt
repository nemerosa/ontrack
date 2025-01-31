package net.nemerosa.ontrack.model.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.coroutines.CoroutineContext

fun launchWithSecurityContext(
    context: CoroutineContext = Dispatchers.Default,
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