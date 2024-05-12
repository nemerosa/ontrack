package net.nemerosa.ontrack.model.tx

/**
 * This component can be used by services to create ad-hoc transactions.
 */
interface TransactionHelper {

    /**
     * Runs some code into a new transaction.
     *
     * When running in test mode, no new transaction is created.
     */
    fun <T> inNewTransaction(code: () -> T): T

}