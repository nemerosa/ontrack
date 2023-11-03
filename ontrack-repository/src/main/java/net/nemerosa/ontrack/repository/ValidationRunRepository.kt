package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunData

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

}