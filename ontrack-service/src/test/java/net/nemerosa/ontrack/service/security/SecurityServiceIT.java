package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Function;

import static org.junit.Assert.*;

public class SecurityServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private SecurityService securityService;

    @Test
    public void getCurrentAccount() throws Exception {
        Account account = asUser().call(securityService::getCurrentAccount);
        assertNotNull(account);
    }

    @Test
    public void getCurrentAccount_none() throws Exception {
        Account account = securityService.getCurrentAccount();
        assertNull(account);
    }

    @Test
    public void runner_function() throws Exception {
        // Function that needs an authentication context
        Function<String, String> fn = (s) -> s + " -> " + getContextName();
        // Testing outside a context
        assertEquals("test -> none", fn.apply("test"));
        // With a context
        Function<String, String> securedFn = asUser().with(ProjectCreation.class).call(() -> securityService.runner(fn));
        // Calls the secured function
        assertEquals("test -> TestingAuthenticationToken", securedFn.apply("test"));
    }

    private static String getContextName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getClass().getSimpleName() : "none";
    }

}
