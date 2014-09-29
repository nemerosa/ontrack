package net.nemerosa.ontrack.service.security.ldap

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource

@Configuration
@ImportResource("classpath:ldap-config.xml")
class LDAPIntegrationTestConfig {
}
