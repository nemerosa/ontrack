package net.nemerosa.ontrack.extension.hook.records

data class HookRecordQueryFilter(
        val id: String? = null,
        val hook: String? = null,
        val state: HookRecordState? = null,
        val text: String? = null,
)
