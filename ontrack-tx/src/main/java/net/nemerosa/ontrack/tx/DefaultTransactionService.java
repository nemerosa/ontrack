package net.nemerosa.ontrack.tx;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.springframework.stereotype.Service;

import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DefaultTransactionService implements TransactionService {

    private final ThreadLocal<Stack<ITransaction>> transaction = new ThreadLocal<>();

    @Override
    public Transaction start() {
        return start(false);
    }

    @Override
    public Transaction start(boolean nested) {
        Stack<ITransaction> currents = transaction.get();
        if (currents == null || currents.isEmpty()) {
            // Creates a new transaction
            ITransaction current = createTransaction();
            // Registers it
            currents = new Stack<>();
            currents.push(current);
            transaction.set(currents);
            // OK
            return current;
        } else if (nested) {
            // Creates a new transaction
            ITransaction current = createTransaction();
            // Registers it
            currents.push(current);
            // OK
            return current;
        } else {
            // Reuses the same transaction
            currents.peek().reuse();
            return currents.peek();
        }
    }

    @Override
    public Transaction get() {
        Stack<ITransaction> stack = transaction.get();
        if (stack != null) {
            return stack.peek();
        } else {
            return null;
        }
    }

    protected ITransaction createTransaction() {
        // Creates the transaction
        return new TransactionImpl(tx -> {
            Stack<ITransaction> stack = transaction.get();
            stack.pop();
            if (stack.isEmpty()) {
                transaction.set(null);
            }
        });
    }

    private interface ITransaction extends Transaction {

        void reuse();

    }

    private interface TransactionCallback {

        void remove(ITransaction tx);

    }

    private static class TransactionImpl implements ITransaction {

        private final TransactionCallback transactionCallback;
        private final AtomicInteger count = new AtomicInteger(1);
        private final Table<Class<? extends TransactionResource>, Object, TransactionResource> resources = Tables
                .newCustomTable(
                        new ConcurrentHashMap<>(),
                        ConcurrentHashMap::new
                );

        public TransactionImpl(TransactionCallback transactionCallback) {
            this.transactionCallback = transactionCallback;
        }

        @Override
        public void close() {
            int value = count.decrementAndGet();
            if (value == 0) {
                // Removes the transaction
                transactionCallback.remove(this);
                // Disposal
                for (TransactionResource resource : resources.values()) {
                    resource.close();
                }
            }
        }

        @Override
        public synchronized <T extends TransactionResource> T getResource(Class<T> resourceType, Object resourceId, TransactionResourceProvider<T> provider) {
            @SuppressWarnings("unchecked")
            T resource = (T) resources.get(resourceType, resourceId);
            if (resource == null) {
                resource = provider.createTxResource();
                resources.put(resourceType, resourceId, resource);
            }
            return resource;
        }

        @Override
        public void reuse() {
            count.incrementAndGet();
        }

    }
}
