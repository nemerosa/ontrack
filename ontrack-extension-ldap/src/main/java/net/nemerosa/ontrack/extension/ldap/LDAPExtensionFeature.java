package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class LDAPExtensionFeature extends AbstractExtensionFeature {
    public LDAPExtensionFeature() {
        super("ldap", "LDAP", "LDAP support for authentication and authorisations");
    }
}
