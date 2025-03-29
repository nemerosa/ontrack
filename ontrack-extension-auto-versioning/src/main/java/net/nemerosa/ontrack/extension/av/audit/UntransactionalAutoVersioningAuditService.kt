package net.nemerosa.ontrack.extension.av.audit

import jakarta.annotation.PostConstruct
import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Same code than for [AutoVersioningAuditServiceImpl] but uses the same transaction, which allows
 * for integration tests.
 *
 * @see AutoVersioningAuditServiceImpl
 */
@Service
@Profile(RunProfile.UNIT_TEST)
@Transactional(propagation = Propagation.REQUIRED)
class UntransactionalAutoVersioningAuditService(
    store: AutoVersioningAuditStore,
) : AbstractAutoVersioningAuditService(store) {

    @PostConstruct
    fun logging() {
        logger.warn("[auto-versioning] Using test auto versioning audit service")
    }

}