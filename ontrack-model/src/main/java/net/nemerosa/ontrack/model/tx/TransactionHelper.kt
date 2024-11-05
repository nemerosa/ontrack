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
    fun <T: Any> inNewTransaction(code: () -> T): T

    /**
     * Runs some code into a new transaction.
     *
     * When running in test mode, no new transaction is created.
     */
    fun <T: Any> inNewTransactionNullable(code: () -> T?): T?

}