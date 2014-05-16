package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.annotations.GlobalFunction;
import net.nemerosa.ontrack.model.security.SecuredUser;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class DefaultSecurityService implements SecurityService {

    @Override
    public void checkGlobalFunction(Class<? extends GlobalFunction> fn) {
        if (!isGlobalFunctionGranted(fn)) {
            throw new AccessDeniedException(format("Global function '%s' is not granted.", fn.getSimpleName()));
        }
    }

    @Override
    public boolean isGlobalFunctionGranted(Class<? extends GlobalFunction> fn) {
        // FIXME Gets the user
        SecuredUser user = null;
        // Checks
        //noinspection ConstantConditions
        return user != null && user.isGranted(fn);
    }
}
