package net.nemerosa.ontrack.boot.support;

import net.nemerosa.ontrack.model.security.UserSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import java.security.SecureRandom;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private APIBasicAuthenticationEntryPoint apiBasicAuthenticationEntryPoint;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private List<UserSource> userSources;

    /**
     * By default, all queries are accessible anonymously. Security is enforced at service level.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Gets a secure random key for the remember be token key
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[64];
        random.nextBytes(randomBytes);
        String rememberBeKey = new String(Hex.encode(randomBytes));
        // @formatter:off
        http.antMatcher("/**")
            // Only BASIC authentication
            .httpBasic()
                .authenticationEntryPoint(apiBasicAuthenticationEntryPoint)
                .realmName("ontrack")
                .and()
            // Logout set-up
            .logout()
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/user/logged-out")
                .addLogoutHandler(basicRememberMeUserDetailsService())
                .and()
            // FIXME CSRF protection for a stateless API?
            //.csrf().requireCsrfProtectionMatcher(new CSRFRequestMatcher()).and()
            .csrf().disable()
            // Allows all at Web level
            .authorizeRequests()
                .antMatchers("/**").permitAll()
            // Remember be authentication token
            .and().rememberMe()
                .rememberMeServices(rememberMeServices(rememberBeKey))
                .key(rememberBeKey)
                .tokenValiditySeconds(604800)
            // Cache enabled
            .and().headers().cacheControl().disable()
        ;
        // @formatter:on
    }

    @Bean
    public RememberMeServices rememberMeServices(String rememberBeKey) {
        return new PersistentTokenBasedRememberMeServices(
                rememberBeKey,
                basicRememberMeUserDetailsService(),
                new InMemoryTokenRepositoryImpl()
        );
    }

    @Bean
    public BasicRememberMeUserDetailsService basicRememberMeUserDetailsService() {
        return new BasicRememberMeUserDetailsService(userSources);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(authenticationManager);
    }

}
