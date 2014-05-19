package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.Signature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class SecurityServiceImpl implements SecurityService {

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
    public void checkProjectFunction(int projectId, Class<? extends ProjectFunction> fn) {
        if (!isProjectFunctionGranted(projectId, fn)) {
            throw new AccessDeniedException(format("Project function '%s' is not granted", fn.getSimpleName()));
        }
    }

    @Override
    public boolean isProjectFunctionGranted(int projectId, Class<? extends ProjectFunction> fn) {
        // Gets the user
        Account user = getCurrentAccount();
        // Checks
        return user != null && user.isGranted(projectId, fn);
    }

    @Override
    public Account getCurrentAccount() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && (authentication.getDetails() instanceof AccountHolder)) {
            return ((AccountHolder) authentication.getDetails()).getAccount();
        } else {
            return null;
        }
    }

    @Override
    public Signature getCurrentSignature() {
        Account account = getCurrentAccount();
        if (account != null) {
            return Signature.of(account.getName());
        } else {
            return Signature.none();
        }
    }
}
