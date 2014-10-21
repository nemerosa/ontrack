package net.nemerosa.ontrack.tx;

import java.util.function.Supplier;

public interface TransactionService {

    Transaction start();

    Transaction start(boolean nested);

    Transaction get();

    default <T> T doInTransaction(Supplier<T> task) {
        try (Transaction ignored = start()) {
            return task.get();
        }
    }

}
