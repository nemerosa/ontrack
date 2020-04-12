package net.nemerosa.ontrack.boot.support

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http {
            // Enabling JS Cookies for CSRF protection (for AngularJS)
            csrf {
                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
            }
        }
    }

    //    private final APIBasicAuthenticationEntryPoint apiBasicAuthenticationEntryPoint;
    //
    //    private final AuthenticationManager authenticationManager;
    //
    //    private final List<UserSource> userSources;
    //    public WebSecurityConfig(APIBasicAuthenticationEntryPoint apiBasicAuthenticationEntryPoint, AuthenticationManager authenticationManager, List<UserSource> userSources) {
    //        this.apiBasicAuthenticationEntryPoint = apiBasicAuthenticationEntryPoint;
    //        this.authenticationManager = authenticationManager;
    //        this.userSources = userSources;
    //    }
    //    /**
    //     * By default, all queries are accessible anonymously. Security is enforced at service level.
    //     */
    //    @Override
    //    protected void configure(HttpSecurity http) throws Exception {
    //        // Gets a secure random key for the remember be token key
    //        SecureRandom random = new SecureRandom();
    //        byte[] randomBytes = new byte[64];
    //        random.nextBytes(randomBytes);
    //        String rememberBeKey = new String(Hex.encode(randomBytes));
    //        // @formatter:off
 //        http.antMatcher("/**")
 //            // Only BASIC authentication
 //            .httpBasic()
 //                .authenticationEntryPoint(apiBasicAuthenticationEntryPoint)
 //                .realmName("ontrack")
 //                .and()
 //            // Logout set-up
 //            .logout()
 //                .logoutUrl("/user/logout")
 //                .logoutSuccessUrl("/user/logged-out")
 //                .addLogoutHandler(basicRememberMeUserDetailsService())
 //                .and()
 //            // FIXME CSRF protection for a stateless API?
 //            //.csrf().requireCsrfProtectionMatcher(new CSRFRequestMatcher()).and()
 //            .csrf().disable()
 //            // Allows all at Web level
 //            .authorizeRequests()
 //                .requestMatchers(EndpointRequest.toAnyEndpoint().excluding(
 //                        InfoEndpoint.class,
 //                        HealthEndpoint.class
 //                )).access("hasRole('ADMIN') or @functionBasedSecurity.hasApplicationManagement(authentication)")
 //                .antMatchers("/**").permitAll()
 //            // Remember be authentication token
 //            .and().rememberMe()
 //                .rememberMeServices(rememberMeServices(rememberBeKey))
 //                .key(rememberBeKey)
 //                .tokenValiditySeconds(604800)
 //            // Cache enabled
 //            .and().headers().cacheControl().disable()
 //        ;
 //        // @formatter:on
    //    }
    //
    //    private RememberMeServices rememberMeServices(String rememberBeKey) {
    //        return new PersistentTokenBasedRememberMeServices(
    //                rememberBeKey,
    //                basicRememberMeUserDetailsService(),
    //                new InMemoryTokenRepositoryImpl()
    //        );
    //    }
    //
    //    @Bean
    //    public BasicRememberMeUserDetailsService basicRememberMeUserDetailsService() {
    //        return new BasicRememberMeUserDetailsService(userSources);
    //    }
    //
    //    @Override
    //    protected void configure(AuthenticationManagerBuilder auth) {
    //        auth.parentAuthenticationManager(authenticationManager);
    //    }
}