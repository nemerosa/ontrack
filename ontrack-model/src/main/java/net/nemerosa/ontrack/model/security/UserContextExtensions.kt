package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.security.access.AccessDeniedException
import kotlin.reflect.KClass

/**
 * Inline variant of [UserContext.isGlobalFunctionGranted]
 */
inline fun <reified T : GlobalFunction> UserContext.isGlobalFunctionGranted() =
    isGlobalFunctionGranted(T::class)

/**
 * Checking the access to a global function
 */
fun UserContext.checkGlobalFunction(fn: KClass<out GlobalFunction>) {
    if (!isGlobalFunctionGranted(fn)) {
        throw AccessDeniedException("Global function '${fn.simpleName}' is not granted.")
    }
}

/**
 * Checking the access to a global function (inline variant)
 */
inline fun <reified T : GlobalFunction> UserContext.checkGlobalFunction() {
    checkGlobalFunction(T::class)
}

/**
 * Inline variant of [UserContext.isProjectFunctionGranted]
 */
inline fun <reified T : ProjectFunction> UserContext.isProjectFunctionGranted(entity: ProjectEntity) =
    isProjectFunctionGranted(entity.project.id(), T::class)
