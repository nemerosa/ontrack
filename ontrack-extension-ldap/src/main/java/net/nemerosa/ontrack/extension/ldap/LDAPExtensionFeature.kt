package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class LDAPExtensionFeature : AbstractExtensionFeature(
        "ldap",
        "LDAP",
        "LDAP support for authentication and authorisations",
        ExtensionFeatureOptions.DEFAULT.withGui(true)
)
