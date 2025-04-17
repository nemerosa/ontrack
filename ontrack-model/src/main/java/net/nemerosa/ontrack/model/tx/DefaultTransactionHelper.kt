package net.nemerosa.ontrack.model.tx

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@Component
@Profile(RunProfile.PROD)
class DefaultTransactionHelper(
    private val platformTransactionManager: PlatformTransactionManager,
) : TransactionHelper {

    override fun <T : Any> inNewTransaction(code: () -> T): T =
        executeTransaction(code, allowNull = false)!!

    override fun <T : Any> inNewTransactionNullable(code: () -> T?): T? =
        executeTransaction(code, allowNull = true)

    private fun <T : Any> executeTransaction(
        code: () -> T?,
        allowNull: Boolean
    ): T? {
        val transactionTemplate = TransactionTemplate(platformTransactionManager)
        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        val result = transactionTemplate.execute {
            code()
        }
        if (!allowNull && result == null) {
            error("Unexpected transaction result: null")
        }
        return result
    }
}
