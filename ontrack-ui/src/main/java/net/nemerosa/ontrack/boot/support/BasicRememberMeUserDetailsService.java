package net.nemerosa.ontrack.boot.support;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class BasicRememberMeUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // FIXME Returns a AccountUserDetails
        // FIXME Loads the user details using a provider
        // FIXME Loads the user groups from the LDAP. How?
        return new User(username, "", Collections.emptyList());
    }

}
