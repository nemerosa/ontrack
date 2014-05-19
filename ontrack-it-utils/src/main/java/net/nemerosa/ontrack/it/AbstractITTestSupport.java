package net.nemerosa.ontrack.it;

import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.test.TestUtils;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.concurrent.Callable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = AbstractITTestSupport.AbstractIntegrationTestConfiguration.class)
@ActiveProfiles(profiles = {RunProfile.UNIT_TEST})
public abstract class AbstractITTestSupport extends AbstractJUnit4SpringContextTests {

    @Configuration
    @Profile(RunProfile.UNIT_TEST)
    @ComponentScan("net.nemerosa.ontrack")
    public static class AbstractIntegrationTestConfiguration {
    }

    public static NameDescription nameDescription() {
        String uid = TestUtils.uid("");
        return new NameDescription(
                uid,
                String.format("%s description", uid)
        );
    }

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
            account = Account.of(name, role);
        }

        @Override
        protected void contextSetup() {
            SecurityContext context = new SecurityContextImpl();
            TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                    (AccountHolder) () -> account,
                    "",
                    account.getRole().name()
            );
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
    }

    protected static class UserCall extends AccountCall {

        public UserCall() {
            super("user", SecurityRole.USER);
        }

        public UserCall with(Class<? extends GlobalFunction> fn) {
            account.with(fn);
            return this;
        }

        public UserCall with(int projectId, Class<? extends ProjectFunction> fn) {
            account.with(projectId, fn);
            return this;
        }

    }
}
