package net.nemerosa.ontrack.model.security;

import java.util.function.Supplier;

/**
 * Interface used by the {@link net.nemerosa.ontrack.model.security.SecurityService} to allow
 * services to run with additional authorizations.
 */
public interface RunAs {

    RunAs with(Class<? extends GlobalFunction> fn);

    <T> Supplier<T> runFor(Supplier<T> call);
}
