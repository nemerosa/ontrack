package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.SecurityService

fun <T> AutoVersioningAuditStore.withSignatureDaysOlder(
    securityService: SecurityService,
    days: Int,
    code: () -> T,
): T {
    val impl = this as AutoVersioningAuditStoreImpl
    val oldProvider = impl.signatureProvider
    return try {
        impl.signatureProvider = {
            securityService.currentSignature.withTime(
                Time.now().minusDays(days.toLong())
            )
        }
        code()
    } finally {
        impl.signatureProvider = oldProvider
    }
}