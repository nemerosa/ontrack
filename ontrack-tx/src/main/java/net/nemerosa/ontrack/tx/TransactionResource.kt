package net.nemerosa.ontrack.tx

interface TransactionResource : AutoCloseable {

    override fun close()

}
