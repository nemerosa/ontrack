package net.nemerosa.ontrack.tx

@FunctionalInterface
interface TransactionResourceProvider<T : TransactionResource> {

    fun createTxResource(): T

}
