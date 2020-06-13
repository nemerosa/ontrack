package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * More Kotlin-friendly call to [SecurityService]
 */
@Deprecated("Native methods can be used instead.", replaceWith = ReplaceWith("asAdmin"))
fun <T> SecurityService.callAsAdmin(call: () -> T): T = asAdmin(call)

inline fun <reified F : ProjectFunction> SecurityService.isProjectFunctionGranted(e: ProjectEntity) =
        isProjectFunctionGranted(e, F::class.java)

inline fun <reified F : GlobalFunction> SecurityService.isGlobalFunctionGranted() =
        isGlobalFunctionGranted(F::class.java)
