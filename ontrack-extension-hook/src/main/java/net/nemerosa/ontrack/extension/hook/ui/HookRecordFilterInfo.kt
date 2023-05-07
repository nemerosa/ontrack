package net.nemerosa.ontrack.extension.hook.ui

import net.nemerosa.ontrack.model.annotations.APIDescription

data class HookRecordFilterInfo(
    @APIDescription("List of endpoints")
    val hooks: List<String>,
    @APIDescription("List of available hook messages states")
    val states: List<String>,
)