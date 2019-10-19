package net.nemerosa.ontrack.tx;

public interface Transaction extends AutoCloseable {

    void close();

    <T extends TransactionResource> T getResource(Class<T> resourceType, Object resourceId, TransactionResourceProvider<T> provider);

}
