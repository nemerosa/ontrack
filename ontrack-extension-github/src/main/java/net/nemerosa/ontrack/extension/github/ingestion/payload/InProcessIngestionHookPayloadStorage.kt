package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.transaction.annotation.Transactional

/**
 * To be used for testing only.
 *
 * No new transaction is required for this.
 */
@Transactional
class InProcessIngestionHookPayloadStorage(
    storageService: StorageService,
    securityService: SecurityService,
) : AbstractInternalIngestionHookPayloadStorage(storageService, securityService)
