package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.extension.api.UISecurityExtension
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    /**
     * Management end points are accessible on a separate port without any authentication needed
     */
    @Bean
    @Order(1)
    @ConditionalOnWebApplication
    fun actuatorWebSecurity(http: HttpSecurity): SecurityFilterChain {
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
        return http.build()
    }

    /**
     * API login
     */
    @Bean
    @Order(2)
    @ConditionalOnWebApplication
    fun apiWebSecurity(
        http: HttpSecurity,
        tokensService: TokensService,
        authenticationManager: AuthenticationManager,
    ): SecurityFilterChain {
        http {
            securityMatcher("/rest/**")
            securityMatcher("/graphql/**")
            securityMatcher("/extension/**")
            securityMatcher("/hook/secured/**")
            // Disables CSRF for the API calls
            csrf {
                disable()
            }
            // Requires authentication
            authorizeRequests {
                authorize("/hook/secured/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            // TODO CORS only for development
            cors{}
            // Requires BASIC authentication
            httpBasic { }
            // Token based authentication (for API only)
            addFilterAt<BasicAuthenticationFilter>(
                TokenHeaderAuthenticationFilter(authenticationManager, tokensService = tokensService),
            )
        }
        return http.build()
    }

    /**
     * Default UI login
     */
    @Bean
    @ConditionalOnWebApplication
    fun webSecurity(
        http: HttpSecurity,
        uiSecurityExtensions: List<UISecurityExtension>,
    ): SecurityFilterChain {
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
        return http.build()
    }

}