package net.nemerosa.ontrack.boot.support

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.SupplierJwtDecoder
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain
import java.util.function.Supplier

@Configuration
class WebSecurityConfig(
    private val webSecurityFilter: WebSecurityFilter,
    private val tokenSecurityFilter: TokenSecurityFilter,
    private val ontrackConfigProperties: OntrackConfigProperties,
) {

    private val logger: Logger = LoggerFactory.getLogger(WebSecurityConfig::class.java)

    /**
     * Management end points are accessible on a separate port without any authentication needed
     */
    @Bean
    fun actuatorWebSecurity(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests { requests ->
                requests.anyRequest().permitAll()
            }
        return http.build()
    }

    /**
     * API login
     */
    @Bean
    fun apiWebSecurity(
        http: HttpSecurity,
        jwtDecoder: JwtDecoder,
    ): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(anyRequest, authenticated)
            }
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            oauth2ResourceServer {
                jwt {
                    if (ontrackConfigProperties.security.authorization.jwt.typ.isNotBlank()) {
                        this.jwtDecoder = customJwtDecoder(jwtDecoder, ontrackConfigProperties.security.authorization.jwt.typ)
                    }
                }
            }
            addFilterAfter<BearerTokenAuthenticationFilter>(tokenSecurityFilter)
            addFilterAfter<TokenSecurityFilter>(webSecurityFilter)
        }
        return http.build()
    }

    private fun customJwtDecoder(jwtDecoder: JwtDecoder, typ: String): JwtDecoder {
        if (jwtDecoder is NimbusJwtDecoder) {
            @Suppress("UNCHECKED_CAST")
            val jwtProcessor = jwtDecoder::class
                .java
                .getDeclaredField("jwtProcessor")
                .apply { isAccessible = true }
                .get(jwtDecoder) as ConfigurableJWTProcessor<SecurityContext>
            logger.info("Using a custom JWT `typ`: $typ")
            jwtProcessor.jwsTypeVerifier = DefaultJOSEObjectTypeVerifier(
                setOf(
                    JOSEObjectType(typ)
                )
            )
            return jwtDecoder
        } else if (jwtDecoder is SupplierJwtDecoder) {
            @Suppress("UNCHECKED_CAST")
            val delegate = jwtDecoder::class
                .java
                .getDeclaredField("delegate")
                .apply { isAccessible = true }
                .get(jwtDecoder) as Supplier<JwtDecoder>
            val delegateDecoder = delegate.get()
            return customJwtDecoder(delegateDecoder, typ)
        } else {
            return jwtDecoder
        }
    }

}