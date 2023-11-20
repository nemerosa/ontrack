package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.ValidationStampFilterCreate
import net.nemerosa.ontrack.model.security.ValidationStampFilterMgt

/**
 * Checks the authorization to update a validation stamp filter, depending on its scope.
 *
 * @receiver The security service to extend
 * @param filter The filter to check
 */
fun SecurityService.checkUpdateAuthorisations(filter: ValidationStampFilter) {
    filter.project?.let {
        checkProjectFunction(it, ValidationStampFilterMgt::class.java)
    } ?: filter.branch?.let {
        checkProjectFunction(it, ValidationStampFilterCreate::class.java)
    } ?: checkGlobalFunction(GlobalSettings::class.java)
}


/**
 * Gets the authorization to update a validation stamp filter, depending on its scope.
 *
 * @receiver The security service to extend
 * @param filter The filter to check
 * @return If the current user is authorized
 */
fun SecurityService.isUpdateAuthorized(filter: ValidationStampFilter) =
    filter.project?.let {
        isProjectFunctionGranted(it, ValidationStampFilterMgt::class.java)
    } ?: filter.branch?.let {
        isProjectFunctionGranted(it, ValidationStampFilterCreate::class.java)
    } ?: isGlobalFunctionGranted(GlobalSettings::class.java)
