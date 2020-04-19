package net.nemerosa.ontrack.boot.support

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    /**
     * API login
     */
    @Configuration
    @Order(1)
    class ApiWebSecurityConfigurationAdapter: WebSecurityConfigurerAdapter() {
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
                // TODO Token based authentication
            }
        }
    }

    /**
     * Default UI login
     */
    @Configuration
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
                // Requires authentication always
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
                // Using a form login
                formLogin { }
                // Logout setup
                logout {
                    logoutUrl = "/logout"
                }
            }
        }
    }

}