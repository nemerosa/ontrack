package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.ValidationRun

/**
 * This extension allows to register metrics about validation runs (and their data)
 * in an external system.
 */
interface ValidationRunMetricsExtension : Extension {

    fun onValidationRun(validationRun: ValidationRun)

}