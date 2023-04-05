package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Service
@Profile("!${RunProfile.UNIT_TEST}")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class DefaultHookRecordService(store: HookRecordStore) : AbstractHookRecordService(store) {

    @PostConstruct
    fun logging() {
        logger.info("[hook] Using production hook record service")
    }

}