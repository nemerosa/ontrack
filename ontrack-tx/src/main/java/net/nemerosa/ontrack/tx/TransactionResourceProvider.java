package net.nemerosa.ontrack.tx;

@FunctionalInterface
public interface TransactionResourceProvider<T extends TransactionResource> {

    T createTxResource();

}
