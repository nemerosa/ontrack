package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // Gets the user
        Account user = getCurrentAccount();
        // Checks
        return user != null && user.isGranted(fn);
    }

    @Override
    public Account getCurrentAccount() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && (authentication.getDetails() instanceof Account)) {
            return (Account) authentication.getDetails();
        } else {
            return null;
        }
    }
}
