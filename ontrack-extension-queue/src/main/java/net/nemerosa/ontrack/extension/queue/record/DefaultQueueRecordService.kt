package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Service
@Profile("!${RunProfile.UNIT_TEST}")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class DefaultQueueRecordService(
    queueRecordStore: QueueRecordStore
): AbstractQueueRecordService(queueRecordStore) {

    @PostConstruct
    fun logging() {
        logger.info("[queue] Using production queue record service")
    }
}