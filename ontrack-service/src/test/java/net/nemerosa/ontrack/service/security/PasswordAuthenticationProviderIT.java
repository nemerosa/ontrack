package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.it.AbstractITTestSupport;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.model.structure.Entity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.Assert.*;

public class PasswordAuthenticationProviderIT extends AbstractITTestSupport {

    @Autowired
    @Qualifier("password")
    private PasswordAuthenticationProvider provider;

    @Test
    public void admin() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("admin", "admin");
        // Retrieval
        UserDetails userDetails = provider.retrieveUser("admin", token);
        // Password check
        provider.additionalAuthenticationChecks(userDetails, token);
        // Checks
        assertNotNull(userDetails);
        assertNull(userDetails.getPassword());
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails instanceof AccountUserDetails);
        AccountUserDetails accountUserDetails = (AccountUserDetails) userDetails;
        Account account = accountUserDetails.getAccount();
        Entity.isEntityDefined(account, "Account must be defined");
        assertEquals("admin", account.getName());
        assertEquals(SecurityRole.ADMINISTRATOR, account.getRole());
    }

}
