package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager;
import net.nemerosa.ontrack.model.settings.CachedSettingsService;
import net.nemerosa.ontrack.model.support.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LDAPSettingsManager extends AbstractSettingsManager<LDAPSettings> {

    private final SettingsRepository settingsRepository;
    private final EncryptionService encryptionService;
    private final LDAPProviderFactory ldapProviderFactory;

    @Autowired
    public LDAPSettingsManager(CachedSettingsService cachedSettingsService, SecurityService securityService, SettingsRepository settingsRepository, EncryptionService encryptionService, LDAPProviderFactory ldapProviderFactory) {
        super(LDAPSettings.class, cachedSettingsService, securityService);
        this.settingsRepository = settingsRepository;
        this.encryptionService = encryptionService;
        this.ldapProviderFactory = ldapProviderFactory;
    }

    @Override
    protected void doSaveSettings(LDAPSettings settings) {
        ldapProviderFactory.invalidate();
        settingsRepository.setBoolean(LDAPSettings.class, "enabled", settings.isEnabled());
        if (settings.isEnabled()) {
            settingsRepository.setString(LDAPSettings.class, "url", settings.getUrl());
            settingsRepository.setString(LDAPSettings.class, "searchBase", settings.getSearchBase());
            settingsRepository.setString(LDAPSettings.class, "searchFilter", settings.getSearchFilter());
            settingsRepository.setString(LDAPSettings.class, "user", settings.getUser());
            settingsRepository.setPassword(LDAPSettings.class, "password", settings.getPassword(), true, encryptionService::encrypt);
            settingsRepository.setString(LDAPSettings.class, "fullNameAttribute", Objects.toString(settings.getFullNameAttribute(), ""));
            settingsRepository.setString(LDAPSettings.class, "emailAttribute", Objects.toString(settings.getEmailAttribute(), ""));
            settingsRepository.setString(LDAPSettings.class, "groupAttribute", Objects.toString(settings.getGroupAttribute(), ""));
            settingsRepository.setString(LDAPSettings.class, "groupFilter", Objects.toString(settings.getGroupFilter(), ""));
        }
    }

    @Override
    protected Form getSettingsForm(LDAPSettings settings) {
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
                                .help("URL to the LDAP server. For example: https://ldap.nemerosa.com:636")
                                .value(settings.getUrl())
                )
                .with(
                        Text.of("user")
                                .visibleIf("enabled")
                                .label("User")
                                .help("Name of the user used to connect to the LDAP server.")
                                .optional()
                                .value(settings.getUser())
                )
                .with(
                        Password.of("password")
                                .visibleIf("enabled")
                                .label("Password")
                                .help("Password of the user used to connect to the LDAP server.")
                                .optional()
                                .value("") // Password never sent to the client
                )
                .with(
                        Text.of("searchBase")
                                .visibleIf("enabled")
                                .label("Search base")
                                .help("Query to get the user. For example: dc=nemerosa,dc=com")
                                .optional()
                                .value(settings.getSearchBase())
                )
                .with(
                        Text.of("searchFilter")
                                .visibleIf("enabled")
                                .label("Search filter")
                                .help("Filter on the user query. {0} will be replaced by the user name. For example: (sAMAccountName={0})")
                                .optional()
                                .value(settings.getSearchFilter())
                )
                .with(
                        Text.of("fullNameAttribute")
                                .visibleIf("enabled")
                                .label("Full name attribute")
                                .help("Name of the attribute that contains the full name of the user. Defaults to cn")
                                .optional()
                                .value(settings.getFullNameAttribute())
                )
                .with(
                        Text.of("emailAttribute")
                                .visibleIf("enabled")
                                .label("Email attribute")
                                .help("Name of the attribute that contains the email of the user. Defaults to email")
                                .optional()
                                .value(settings.getEmailAttribute())
                )
                .with(
                        Text.of("groupAttribute")
                                .visibleIf("enabled")
                                .label("Group attribute")
                                .help("Name of the attribute that contains the groups the user belongs to. Defaults to memberOf)")
                                .optional()
                                .value(settings.getGroupAttribute())
                )
                .with(
                        Text.of("groupFilter")
                                .visibleIf("enabled")
                                .label("Group filter")
                                .help("Name of the OU field used to filter groups a user belongs to (optional).")
                                .optional()
                                .value(settings.getGroupFilter())
                )
                ;
    }

    @Override
    public String getId() {
        return "ldap";
    }

    @Override
    public String getTitle() {
        return "LDAP settings";
    }
}
