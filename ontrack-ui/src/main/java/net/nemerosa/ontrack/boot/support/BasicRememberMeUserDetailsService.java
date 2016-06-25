package net.nemerosa.ontrack.boot.support;

import net.nemerosa.ontrack.model.security.UserSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

public class BasicRememberMeUserDetailsService implements UserDetailsService, LogoutHandler {

    private final List<UserSource> userSources;

    public BasicRememberMeUserDetailsService(List<UserSource> userSources) {
        this.userSources = userSources;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userSources.stream()
                .map(userSource -> userSource.loadUser(username))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Cannot load user using its name"));
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        userSources.forEach(userSource -> userSource.onLogout(authentication.getName()));
    }
}
