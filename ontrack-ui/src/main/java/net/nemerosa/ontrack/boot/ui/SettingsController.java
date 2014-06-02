package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.GlobalSettingsExtension;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.DescribedForm;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Security
     */
    @RequestMapping(value = "security", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    // TODO Security settings
    public Ack updateSecurity() {
        return Ack.OK;
    }


    private DescribedForm getSecuritySettingsForm() {
        // TODO Gets the security settings
        return DescribedForm.create(
                "security",
                Form.create()
                        .with(
                                YesNo.of("grantProjectViewToAll")
                                        .label("Grants project view to all")
                                        .help("Unless disabled at project level, this would enable any user (even anonymous) " +
                                                "to view the content of all projects.")
                                                // TODO Gets the value from the settings
                                        .value(false)
                        )
        ).title("Security").description("Global settings for the security.")
                .uri(uri(on(getClass()).updateSecurity()));
    }

}
