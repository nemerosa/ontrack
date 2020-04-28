package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import org.springframework.stereotype.Component

@Component
class LDAPExtensionFeature : AbstractExtensionFeature(
        "ldap",
        "LDAP",
        "LDAP support for authentication and authorisations"
)
