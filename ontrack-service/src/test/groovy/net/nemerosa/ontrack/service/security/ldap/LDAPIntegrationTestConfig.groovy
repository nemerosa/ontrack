package net.nemerosa.ontrack.service.security.ldap

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.Profile

/**
 * Configuration for the LDAP integration tests
 */
@Configuration
@Profile(RunProfile.UNIT_TEST)
@ImportResource("classpath:ldap-config.xml")
class LDAPIntegrationTestConfig {
}
