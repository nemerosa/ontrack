package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class LDAPExtensionFeature extends AbstractExtensionFeature {

    /**
     * Identifier of the LDAP group mapping type.
     *
     * @see net.nemerosa.ontrack.model.security.AccountGroupMappingService
     */
    public static final String LDAP_GROUP_MAPPING = "ldap";

    public LDAPExtensionFeature() {
        super("ldap", "LDAP", "LDAP support for authentication and authorisations");
    }
}
