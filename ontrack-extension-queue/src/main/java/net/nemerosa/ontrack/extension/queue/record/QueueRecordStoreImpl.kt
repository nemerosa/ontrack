package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component

@Component
class QueueRecordStoreImpl(
    private val store: StorageService,
) : QueueRecordStore {

    override fun start(queuePayload: QueuePayload) {
        val record = QueueRecord.create(queuePayload)
        store.store(
            STORE,
            queuePayload.id,
            record
        )
    }

    override fun save(queuePayload: QueuePayload, code: (QueueRecord) -> QueueRecord) {
        val oldRecord = getRecord(queuePayload.id)
        val newRecord = code(oldRecord)
        store.store(STORE, queuePayload.id, newRecord)
    }

    private fun getRecord(id: String): QueueRecord =
        store.find(STORE, id, QueueRecord::class)
            ?: throw QueueRecordNotFoundException(id)

    companion object {
        private val STORE = QueueRecordStore::class.java.name
    }
}