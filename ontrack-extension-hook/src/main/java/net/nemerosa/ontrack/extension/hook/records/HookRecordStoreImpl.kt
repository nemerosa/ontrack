package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component

@Component
class HookRecordStoreImpl(
        private val store: StorageService,
) : HookRecordStore {

    override fun save(record: HookRecord) {
        store.store(
                STORE,
                record.id,
                record
        )
    }

    override fun save(recordId: String, code: (HookRecord) -> HookRecord) {
        val oldRecord = getRecord(recordId)
        val newRecord = code(oldRecord)
        save(newRecord)
    }

    private fun getRecord(recordId: String): HookRecord =
            store.find(STORE, recordId, HookRecord::class)
                    ?: throw HookRecordNotFoundException(recordId)

    companion object {
        private val STORE = HookRecordStore::class.java.name
    }
}