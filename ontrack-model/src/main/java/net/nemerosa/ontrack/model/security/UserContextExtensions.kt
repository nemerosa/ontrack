package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Inline variant of [UserContext.isGlobalFunctionGranted]
 */
inline fun <reified T : GlobalFunction> UserContext.isGlobalFunctionGranted() =
    isGlobalFunctionGranted(T::class)

/**
 * Inline variant of [UserContext.isProjectFunctionGranted]
 */
inline fun <reified T : ProjectFunction> UserContext.isProjectFunctionGranted(entity: ProjectEntity) =
    isProjectFunctionGranted(entity.project.id(), T::class)
