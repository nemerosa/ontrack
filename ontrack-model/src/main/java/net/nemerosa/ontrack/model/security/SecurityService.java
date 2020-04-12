package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Signature;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface SecurityService {

    void checkGlobalFunction(Class<? extends GlobalFunction> fn);

    boolean isGlobalFunctionGranted(Class<? extends GlobalFunction> fn);

    void checkProjectFunction(int projectId, Class<? extends ProjectFunction> fn);

    default void checkProjectFunction(ProjectEntity entity, Class<? extends ProjectFunction> fn) {
        checkProjectFunction(entity.projectId(), fn);
    }

    boolean isProjectFunctionGranted(int projectId, Class<? extends ProjectFunction> fn);

    default boolean isProjectFunctionGranted(ProjectEntity entity, Class<? extends ProjectFunction> fn) {
        return isProjectFunctionGranted(entity.projectId(), fn);
    }

    /**
     * Returns the current logged account or <code>null</code> if none is logged.
     */
    Account getCurrentAccount();

    /**
     * Is the current user logged?
     */
    default boolean isLogged() {
        return getCurrentAccount() != null;
    }

    /**
     * Returns the current logged account as an option
     */
    default Optional<Account> getAccount() {
        return Optional.ofNullable(getCurrentAccount());
    }

    Signature getCurrentSignature();

    /**
     * Performs a call as admin. This is mainly by internal code to access parts of the application
     * which are usually protected from the current context, but that need to be accessed locally.
     *
     * @param supplier Call to perform in a protected context
     * @param <T>      Type of data to get back
     * @return A new supplier running in a new security context
     */
    <T> Supplier<T> runAsAdmin(Supplier<T> supplier);

    <T> T asAdmin(Supplier<T> supplier);

    void asAdmin(Runnable task);

    Runnable runAsAdmin(Runnable task);

    /**
     * In some asynchronous operations, we need to run a task with the same credentials that initiated the operation.
     * This method creates a wrapping supplier that holds the initial security context.
     *
     * @param supplier Call to perform in a protected context
     * @param <T>      Type of data to get back
     * @return A new supplier running in a new security context
     */
    <T> Supplier<T> runner(Supplier<T> supplier);

    /**
     * In some asynchronous operations, we need to run a task with the same credentials that initiated the operation.
     * This method creates a wrapping supplier that holds the initial security context.
     *
     * @param fn  Call to perform in a protected context
     * @param <T> Type of data to get back
     * @return A new function running in a new security context
     */
    <T, R> Function<T, R> runner(Function<T, R> fn);
}
