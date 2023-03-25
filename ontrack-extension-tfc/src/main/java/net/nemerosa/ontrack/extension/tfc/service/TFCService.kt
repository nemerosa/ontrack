package net.nemerosa.ontrack.extension.tfc.service

interface TFCService {

    fun validate(
        params: TFCParameters,
        workspaceId: String,
        runUrl: String
    ): TFCValidationResult

}