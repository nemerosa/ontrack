package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.GlobalSettingsExtension;
import net.nemerosa.ontrack.model.form.DescribedForm;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global settings management.
 */
@RestController
@RequestMapping("/settings")
public class SettingsController {

    private final ExtensionManager extensionManager;

    @Autowired
    public SettingsController(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    /**
     * List of forms to configure.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<DescribedForm> configuration() {
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

    private DescribedForm getSecuritySettingsForm() {
        return DescribedForm.create(
                "security",
                Form.create()
                        .with(
                                YesNo.of("grantProjectViewToAll")
                                        .label("Grand project view to all")
                                        .help("Unless disabled at project level, this would enable any user (even anonymous) " +
                                                "to view the content of all projects.")
                                                // TODO Gets the value from the settings
                                        .value(false)
                        )
        ).title("Security").description("Global settings for the security.");
        // TODO URI
    }

}
