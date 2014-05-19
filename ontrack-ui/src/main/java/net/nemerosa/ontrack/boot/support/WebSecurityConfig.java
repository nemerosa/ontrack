package net.nemerosa.ontrack.boot.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private APIBasicAuthenticationEntryPoint apiBasicAuthenticationEntryPoint;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * By default, all queries are accessible anonymously. Security is enforced at service level.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
                .and()
            // FIXME CSRF protection for a stateless API?
            //.csrf().requireCsrfProtectionMatcher(new CSRFRequestMatcher()).and()
            .csrf().disable()
            // Allows all at Web level
            .authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
        ;
        // @formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(authenticationManager);
    }

}
