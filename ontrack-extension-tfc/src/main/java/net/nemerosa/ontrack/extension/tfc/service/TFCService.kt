package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.model.structure.ValidationRunStatusID

interface TFCService {

    fun validate(
        params: TFCParameters,
        status: ValidationRunStatusID,
        workspaceId: String,
        runUrl: String
    ): TFCValidationResult

}