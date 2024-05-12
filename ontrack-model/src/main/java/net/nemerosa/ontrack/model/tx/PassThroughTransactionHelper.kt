package net.nemerosa.ontrack.model.tx

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * This implementation, used for testing only, reuses the
 * current transaction and does not create a new one.
 */
@Component
@Profile(RunProfile.UNIT_TEST)
class PassThroughTransactionHelper : TransactionHelper {

    override fun <T> inNewTransaction(code: () -> T): T {
        return code()
    }

}
