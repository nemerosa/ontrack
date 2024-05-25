package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunData
import net.nemerosa.ontrack.model.structure.ValidationStamp

interface ValidationRunRepository {

    /**
     * Updates an existing validation run with new data.
     *
     * Any existing data will be overridden with the new one or deleted.
     *
     * @param run Existing validation run.
     * @param data New validation data to set (can be null to delete any existing data)
     * @return Updated validation run
     */
    fun updateValidationRunData(
        run: ValidationRun,
        data: ValidationRunData<*>?,
    ): ValidationRun

    /**
     * Checks if the last run for the given [build] and [validation stamp][validationStamp], if
     * it exists, is passed or not.
     *
     * If there was no run at all or the last one is not passed, the function returns `false`.
     */
    fun isValidationRunPassed(build: Build, validationStamp: ValidationStamp): Boolean

}