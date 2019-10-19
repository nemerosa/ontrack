package net.nemerosa.ontrack.tx

interface TransactionService {

    fun start(): Transaction

    fun start(nested: Boolean): Transaction

    fun get(): Transaction

    fun <T> doInTransaction(task: () -> T): T {
        start().use { _ -> return task() }
    }

}
