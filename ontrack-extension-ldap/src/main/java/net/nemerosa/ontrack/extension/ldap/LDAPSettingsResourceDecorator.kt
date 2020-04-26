package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import org.springframework.stereotype.Component

/**
 * Obfuscation of the password.
 */
@Component
class LDAPSettingsResourceDecorator : AbstractResourceDecorator<LDAPSettings>(LDAPSettings::class.java) {

    override fun decorateBeforeSerialization(bean: LDAPSettings): LDAPSettings = bean.withPassword("")

}