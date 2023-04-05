package net.nemerosa.ontrack.extension.hook.records

interface HookRecordStore {
    fun save(record: HookRecord)
    fun save(recordId: String, code: (HookRecord) -> HookRecord)
}