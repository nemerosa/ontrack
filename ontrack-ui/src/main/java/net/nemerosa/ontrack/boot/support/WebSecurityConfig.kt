package net.nemerosa.ontrack.boot.support

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    /**
     * API login
     */
    @Configuration
    @Order(1)
    @ConditionalOnWebApplication
    class ApiWebSecurityConfigurationAdapter : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                securityMatcher("/rest/**")
                securityMatcher("/graphql/**")
                // TODO 👇 To migrate to /rest/
                securityMatcher("/accounts/**")
                securityMatcher("/admin/**")
                securityMatcher("/api/**")
                securityMatcher("/structure/**")
                securityMatcher("/events/**")
                securityMatcher("/properties/**")
                securityMatcher("/search/**")
                securityMatcher("/settings/**")
                securityMatcher("/user/**")
                securityMatcher("/extension/**")
                securityMatcher("/extensions/**")
                // Disables CSRF for the API calls
                csrf {
                    disable()
                }
                // Requires authentication
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
                // Requires BASIC authentication
                httpBasic { }
                // Token based authentication (for API only)
                addFilterAt(TokenHeaderAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter::class.java)
            }
        }
    }

    /**
     * Default UI login
     */
    @Configuration
    @ConditionalOnWebApplication
    class UIWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                // Enabling JS Cookies for CSRF protection (for AngularJS)
                csrf {
                    csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
                }
                // Management end points are accessible on a separate port without any authentication needed
                authorizeRequests {
                    authorize(EndpointRequest.toAnyEndpoint(), permitAll)
                }
                // Excludes assets and login page from authentication
                authorizeRequests {
                    authorize("/login", permitAll)
                    authorize("/assets/**", permitAll)
                    authorize("/favicon.ico", permitAll)
                }
                // Requires authentication always
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
                // Using a form login
                formLogin {
                    loginPage = "/login"
                    permitAll()
                    authenticationSuccessHandler = LoginSuccessHandler()
                }
                // Logout setup
                logout {
                    logoutUrl = "/logout"
                }
            }
        }
    }

}