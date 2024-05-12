package net.nemerosa.ontrack.model.tx

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@Component
@Profile("!${RunProfile.UNIT_TEST}")
class DefaultTransactionHelper(
    private val platformTransactionManager: PlatformTransactionManager,
) : TransactionHelper {

    override fun <T> inNewTransaction(code: () -> T): T {
        val transactionTemplate = TransactionTemplate(platformTransactionManager)
        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        return transactionTemplate.execute {
            code()
        } ?: error("Unexpected transaction result: null")
    }

}
