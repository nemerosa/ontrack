package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.GlobalSettingsExtension;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.DescribedForm;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
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

    private final ExtensionManager extensionManager;
    private final SecurityService securityService;

    @Autowired
    public SettingsController(ExtensionManager extensionManager, SecurityService securityService) {
        this.extensionManager = extensionManager;
        this.securityService = securityService;
    }

    /**
     * List of forms to configure.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<DescribedForm> configuration() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        List<DescribedForm> forms = new ArrayList<>();
        // Security settings
        forms.add(getSecuritySettingsForm());
        // Extensions settings
        forms.addAll(
                extensionManager.getExtensions(GlobalSettingsExtension.class).stream()
                        .map(GlobalSettingsExtension::getConfigurationForm)
                        .collect(Collectors.toList())
        );
        // OK
        return forms;
    }

    /**
     * Security
     */
    @RequestMapping(value = "security", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    // TODO Security settings
    public Ack updateSecurity(@RequestBody SecuritySettings securitySettings) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return Ack.OK;
    }


    private DescribedForm getSecuritySettingsForm() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        // TODO Gets the security settings
        SecuritySettings securitySettings = SecuritySettings.of();
        return DescribedForm.create(
                "security",
                securitySettings.form()
        )
                .title("Security")
                .description("Global settings for the security.")
                .uri(uri(on(getClass()).updateSecurity(null)));
    }

}
