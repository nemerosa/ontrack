package net.nemerosa.ontrack.tx

interface Transaction : AutoCloseable {

    override fun close()

    fun <T : TransactionResource> getResource(resourceType: Class<T>, resourceId: Any, provider: TransactionResourceProvider<T>): T

}
