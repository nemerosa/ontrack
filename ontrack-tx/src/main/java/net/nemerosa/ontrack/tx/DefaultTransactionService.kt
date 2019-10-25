package net.nemerosa.ontrack.tx

import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class DefaultTransactionService : TransactionService {

    private val transaction = ThreadLocal<Stack<ITransaction>>()

    override fun start(): Transaction {
        return start(false)
    }

    override fun start(nested: Boolean): Transaction {
        var currents: Stack<ITransaction>? = transaction.get()
        if (currents == null || currents.isEmpty()) {
            // Creates a new transaction
            val current = createTransaction()
            // Registers it
            currents = Stack()
            currents.push(current)
            transaction.set(currents)
            // OK
            return current
        } else if (nested) {
            // Creates a new transaction
            val current = createTransaction()
            // Registers it
            currents.push(current)
            // OK
            return current
        } else {
            // Reuses the same transaction
            currents.peek().reuse()
            return currents.peek()
        }
    }

    override fun get(): Transaction {
        val stack = transaction.get()
        return stack?.peek() as Transaction
    }

    private fun createTransaction(): ITransaction {
        // Creates the transaction
        return TransactionImpl(object : TransactionCallback {
            override fun remove(tx: ITransaction) {
                val stack = transaction.get()
                stack.pop()
                if (stack.isEmpty()) {
                    transaction.set(null)
                }
            }
        })
    }

    private interface ITransaction : Transaction {

        fun reuse()

    }

    private interface TransactionCallback {

        fun remove(tx: ITransaction)

    }

    private class TransactionImpl(private val transactionCallback: TransactionCallback) : ITransaction {
        private val count = AtomicInteger(1)

        private val resources: ConcurrentMap<Pair<Class<out TransactionResource>, Any>, TransactionResource> =
                ConcurrentHashMap()

        override fun close() {
            val value = count.decrementAndGet()
            if (value == 0) {
                // Removes the transaction
                transactionCallback.remove(this)
                // Disposal
                for (resource in resources.values) {
                    resource.close()
                }
            }
        }

        @Synchronized
        override fun <T : TransactionResource> getResource(resourceType: Class<T>, resourceId: Any, provider: TransactionResourceProvider<T>): T {
            @Suppress("UNCHECKED_CAST")
            var resource: T? = resources[resourceType to resourceId] as T?
            if (resource == null) {
                resource = provider.createTxResource()
                resources[resourceType to resource] = resource
            }
            return resource
        }

        override fun reuse() {
            count.incrementAndGet()
        }

    }
}
