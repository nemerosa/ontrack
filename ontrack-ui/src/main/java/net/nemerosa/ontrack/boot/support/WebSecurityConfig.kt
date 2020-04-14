package net.nemerosa.ontrack.boot.support

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    // FIXME API login
//    @Configuration
//    @Order(1)
//    class ApiWebSecurityConfigurationAdapter: WebSecurityConfigurerAdapter() {
//        override fun configure(http: HttpSecurity) {
//            http {
//                securityMatcher("/api/**")
//                authorizeRequests {
//                    authorize(anyRequest, hasRole("ADMIN"))
//                }
//                httpBasic { }
//            }
//        }
//    }

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