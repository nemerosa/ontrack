package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.GlobalSettingsExtension;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.DescribedForm;
import net.nemerosa.ontrack.model.settings.LDAPSettings;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.model.settings.SettingsService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Global settings management.
 */
@RestController
@RequestMapping("/settings")
public class SettingsController extends AbstractResourceController {

    private final SettingsService settingsService;
    private final ExtensionManager extensionManager;

    @Autowired
    public SettingsController(SettingsService settingsService, ExtensionManager extensionManager) {
        this.settingsService = settingsService;
        this.extensionManager = extensionManager;
    }

    /**
     * List of forms to configure.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resources<DescribedForm> configuration() {
        List<DescribedForm> forms = new ArrayList<>();
        // Security settings
        forms.add(getSecuritySettingsForm());
        // LDAP settings
        forms.add(getLDAPSettingsForm());
        // Extensions settings
        forms.addAll(
                extensionManager.getExtensions(GlobalSettingsExtension.class).stream()
                        .map(GlobalSettingsExtension::getConfigurationForm)
                        .collect(Collectors.toList())
        );
        // OK
        return Resources.of(
                forms,
                uri(on(getClass()).configuration())
        );
    }

    /**
     * Security
     */
    @RequestMapping(value = "security", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Ack updateSecurity(@RequestBody SecuritySettings securitySettings) {
        settingsService.saveSecuritySettings(securitySettings);
        return Ack.OK;
    }

    /**
     * LDAP
     */
    @RequestMapping(value = "ldap", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Ack updateLDAP(@RequestBody LDAPSettings ldapSettings) {
        settingsService.saveLDAPSettings(ldapSettings);
        return Ack.OK;
    }


    private DescribedForm getSecuritySettingsForm() {
        SecuritySettings securitySettings = settingsService.getSecuritySettings();
        return DescribedForm.create(
                "security",
                securitySettings.form()
        )
                .title("Security")
                .description("Global settings for the security.")
                .uri(uri(on(getClass()).updateSecurity(null)));
    }


    private DescribedForm getLDAPSettingsForm() {
        LDAPSettings ldapSettings = settingsService.getLDAPSettings();
        return DescribedForm.create(
                "ldap",
                ldapSettings.form()
        )
                .title("LDAP")
                .description("LDAP configuration.")
                .uri(uri(on(getClass()).updateLDAP(null)));
    }

}
