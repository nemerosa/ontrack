package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.ProvidedGroupsService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
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
            private val accountService: AccountService,
            private val securityService: SecurityService,
            private val providedGroupsService: ProvidedGroupsService
    ) : WebSecurityConfigurerAdapter() {
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
                    authorize("/login/**", permitAll)
                    authorize("/assets/**", permitAll)
                    authorize("/favicon.ico", permitAll)
                }
                // Requires authentication always
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
                // OAuth setup
                oauth2Client { }
                oauth2Login {
                    loginPage = "/login"
                    permitAll()
                    authenticationSuccessHandler = LoginSuccessHandler()
                    // TODO Use an extension point for this
                    userInfoEndpoint {
                        oidcUserService = OntrackOidcUserService(accountService, securityService, providedGroupsService)
                    }
                }
                // Logout setup
                logout {
                    logoutUrl = "/logout"
                }
            }
        }
    }

}