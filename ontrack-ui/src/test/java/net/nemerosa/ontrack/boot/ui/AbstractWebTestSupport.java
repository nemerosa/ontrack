package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.Application;
import net.nemerosa.ontrack.it.AbstractITTestSupport;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.ProjectFunction;
import net.nemerosa.ontrack.model.security.SecurityRole;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.concurrent.Callable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringApplicationConfiguration(classes = Application.class)
public abstract class AbstractWebTestSupport extends AbstractITTestSupport {

    protected UserCall asUser() {
        return new UserCall();
    }

    protected static interface ContextCall {
        <T> T call(Callable<T> call) throws Exception;
    }

    protected static abstract class AbstractContextCall implements ContextCall {

        @Override
        public <T> T call(Callable<T> call) throws Exception {
            // Gets the current context
            SecurityContext oldContext = SecurityContextHolder.getContext();
            try {
                // Sets the new context
                contextSetup();
                // Call
                return call.call();
            } finally {
                // Restores the context
                SecurityContextHolder.setContext(oldContext);
            }
        }

        protected abstract void contextSetup();
    }

    protected static class AccountCall extends AbstractContextCall {

        protected final Account account;

        public AccountCall(String name, SecurityRole role) {
            account = mock(Account.class);
            when(account.getName()).thenReturn(name);
            when(account.getRole()).thenReturn(role);
        }

        @Override
        protected void contextSetup() {
            SecurityContext context = new SecurityContextImpl();
            TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                    account.getName(),
                    "",
                    account.getRole().name()
            );
            authentication.setDetails(account);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
    }

    protected static class UserCall extends AccountCall {

        public UserCall() {
            super("user", SecurityRole.USER);
        }

        public UserCall with(Class<? extends GlobalFunction> fn) {
            when(account.isGranted(fn)).thenReturn(true);
            return this;
        }

        public UserCall with(int projectId, Class<? extends ProjectFunction> fn) {
            when(account.isGranted(projectId, fn)).thenReturn(true);
            return this;
        }

    }

}
