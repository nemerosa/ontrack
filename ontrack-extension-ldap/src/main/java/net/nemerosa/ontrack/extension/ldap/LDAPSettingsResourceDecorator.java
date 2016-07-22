package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import org.springframework.stereotype.Component;

/**
 * Obfuscation of the password.
 */
@Component
public class LDAPSettingsResourceDecorator extends AbstractResourceDecorator<LDAPSettings> {

    public LDAPSettingsResourceDecorator() {
        super(LDAPSettings.class);
    }

    @Override
    public LDAPSettings decorateBeforeSerialization(LDAPSettings bean) {
        return bean.withPassword("");
    }
}
