package net.nemerosa.ontrack.extension.recordings

import jakarta.annotation.PostConstruct
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.recordings.store.RecordingsStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Profile("!${RunProfile.UNIT_TEST}")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class DefaultRecordingsService(recordingsStore: RecordingsStore) : AbstractRecordingsService(recordingsStore) {

    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun logging() {
        logger.info("[recordings] Using production recordings service")
    }
}