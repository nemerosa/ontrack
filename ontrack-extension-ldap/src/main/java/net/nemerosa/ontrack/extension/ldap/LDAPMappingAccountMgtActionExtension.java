package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.extension.api.AccountMgtActionExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.support.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LDAPMappingAccountMgtActionExtension extends AbstractExtension implements AccountMgtActionExtension {

    @Autowired
    public LDAPMappingAccountMgtActionExtension(LDAPExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

    @Override
    public Action getAction() {
        return Action.of("ldap-mapping", "LDAP Mapping", "ldap-mapping");
    }

}
