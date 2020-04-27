package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.extension.api.AccountMgtActionExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.settings.CachedSettingsService;
import net.nemerosa.ontrack.model.support.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LDAPMappingAccountMgtActionExtension extends AbstractExtension implements AccountMgtActionExtension {

    private final CachedSettingsService cachedSettingsService;

    @Autowired
    public LDAPMappingAccountMgtActionExtension(LDAPExtensionFeature extensionFeature, CachedSettingsService cachedSettingsService) {
        super(extensionFeature);
        this.cachedSettingsService = cachedSettingsService;
    }

    @Override
    public Action getAction() {
        LDAPSettings settings = cachedSettingsService.getCachedSettings(LDAPSettings.class);
        if (settings.isEnabled()) {
            return Action.of("ldap-mapping", "LDAP Mapping", "ldap-mapping");
        } else {
            return null;
        }
    }

}
