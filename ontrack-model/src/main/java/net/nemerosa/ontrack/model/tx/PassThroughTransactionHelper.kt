package net.nemerosa.ontrack.model.tx

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * This implementation, used for testing only, reuses the
 * current transaction and does not create a new one.
 */
@Component
@Profile(RunProfile.DEV)
class PassThroughTransactionHelper : TransactionHelper {

    override fun <T : Any> inNewTransaction(code: () -> T): T {
        return code()
    }

    override fun <T : Any> inNewTransactionNullable(code: () -> T?): T? {
        return code()
    }
}
