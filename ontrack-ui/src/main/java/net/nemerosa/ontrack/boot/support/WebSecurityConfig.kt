package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.extension.api.UISecurityExtension
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
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
     * Management end points are accessible on a separate port without any authentication needed
     */
    @Configuration
    @Order(1)
    @ConditionalOnWebApplication
    class ActuatorWebSecurityConfigurationAdapter : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                securityMatcher("/manage/**")
                // Disables CSRF for the actuator calls
                csrf {
                    disable()
                }
                authorizeRequests {
                    authorize(EndpointRequest.toAnyEndpoint(), permitAll)
                }
            }
        }
    }

    /**
     * API login
     */
    @Configuration
    @Order(2)
    @ConditionalOnWebApplication
    class ApiWebSecurityConfigurationAdapter(
            private val tokensService: TokensService
    ) : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                securityMatcher("/rest/**")
                securityMatcher("/graphql/**")
                securityMatcher("/extension/**")
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
                addFilterAt(TokenHeaderAuthenticationFilter(authenticationManager(), tokensService = tokensService), BasicAuthenticationFilter::class.java)
            }
        }
    }

    /**
     * Default UI login
     */
    @Configuration
    @ConditionalOnWebApplication
    class UIWebSecurityConfigurerAdapter(
            private val uiSecurityExtensions: List<UISecurityExtension>
    ) : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                // Enabling JS Cookies for CSRF protection (for AngularJS)
                csrf {
                    csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
                }
                // Excludes assets and login page from authentication
                authorizeRequests {
                    authorize("/login/**", permitAll)
                    authorize("/assets/**", permitAll)
                    authorize("/favicon.ico", permitAll)
                }
                // Requires authentication always
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
                // UI extensions
                uiSecurityExtensions.forEach { extension ->
                    extension.configure(this, LoginSuccessHandler())
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