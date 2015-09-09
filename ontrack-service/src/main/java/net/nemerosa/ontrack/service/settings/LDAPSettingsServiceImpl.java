package net.nemerosa.ontrack.service.settings;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.LDAPSettings;
import net.nemerosa.ontrack.model.settings.LDAPSettingsService;
import net.nemerosa.ontrack.common.Caches;
import net.nemerosa.ontrack.service.support.SettingsInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service delegates to an internal cached service in order to avoid the cyclic
 * dependency SecurityService &lt;--&gt; SettingsService. We now have
 * SecurityService --&gt; SettingsInternalService and SettingsService --&gt; SecurityService
 * and SettingsService --&gt; SettingsInternalService
 */
@Service
@Transactional
@Deprecated
public class LDAPSettingsServiceImpl implements LDAPSettingsService {

    private final SecurityService securityService;
    private final SettingsInternalService settingsInternalService;

    @Autowired
    public LDAPSettingsServiceImpl(SecurityService securityService, SettingsInternalService settingsInternalService) {
        this.securityService = securityService;
        this.settingsInternalService = settingsInternalService;
    }

    @Override
    public LDAPSettings getSettings() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return settingsInternalService.getLDAPSettings();
    }

    @Override
    @CacheEvict(value = Caches.LDAP_SETTINGS, allEntries = true)
    public void saveSettings(LDAPSettings ldapSettings) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        settingsInternalService.saveLDAPSettings(ldapSettings);
    }

    @Override
    public Form getSettingsForm() {
        LDAPSettings settings = getSettings();
        return Form.create()
                .with(
                        YesNo.of("enabled")
                                .label("Enable LDAP authentication")
                                .value(settings.isEnabled())
                )
                .with(
                        Text.of("url")
                                .visibleIf("enabled")
                                .label("URL")
                                .help("URL to the LDAP server")
                                .value(settings.getUrl())
                )
                .with(
                        Text.of("user")
                                .visibleIf("enabled")
                                .label("User")
                                .help("Name of the user used to connect to the LDAP server")
                                .optional()
                                .value(settings.getUser())
                )
                .with(
                        Password.of("password")
                                .visibleIf("enabled")
                                .label("Password")
                                .help("Password of the user used to connect to the LDAP server")
                                .optional()
                                .value("") // Password never sent to the client
                )
                .with(
                        Text.of("searchBase")
                                .visibleIf("enabled")
                                .label("Search base")
                                .help("Query to get the user")
                                .optional()
                                .value(settings.getSearchBase())
                )
                .with(
                        Text.of("searchFilter")
                                .visibleIf("enabled")
                                .label("Search filter")
                                .help("Filter on the user query")
                                .optional()
                                .value(settings.getSearchFilter())
                )
                .with(
                        Text.of("fullNameAttribute")
                                .visibleIf("enabled")
                                .label("Full name attribute")
                                .help("Name of the attribute that contains the full name of the user")
                                .optional()
                                .value(settings.getFullNameAttribute())
                )
                .with(
                        Text.of("emailAttribute")
                                .visibleIf("enabled")
                                .label("Email attribute")
                                .help("Name of the attribute that contains the email of the user")
                                .optional()
                                .value(settings.getEmailAttribute())
                )
                .with(
                        Text.of("groupAttribute")
                                .visibleIf("enabled")
                                .label("Group attribute")
                                .help("Name of the attribute that contains the groups the user belongs to (defaults to memberOf)")
                                .optional()
                                .value(settings.getGroupAttribute())
                )
                .with(
                        Text.of("groupFilter")
                                .visibleIf("enabled")
                                .label("Group filter")
                                .help("Name of the OU field used to filter groups a user belongs to (optional)")
                                .optional()
                                .value(settings.getGroupFilter())
                )
                ;
    }
}
