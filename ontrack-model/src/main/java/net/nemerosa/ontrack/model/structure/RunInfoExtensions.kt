package net.nemerosa.ontrack.model.structure

fun RunInfo.toRunInfoInput() =
    RunInfoInput(
        sourceType = sourceType,
        sourceUri = sourceUri,
        triggerType = triggerType,
        triggerData = triggerData,
        runTime = runTime,
    )
