package net.nemerosa.ontrack.boot.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.DescribedForm;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.SettingsManager;
import net.nemerosa.ontrack.model.settings.SettingsManagerNotFoundException;
import net.nemerosa.ontrack.model.settings.SettingsValidationException;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Global settings management.
 */
@RestController
@RequestMapping("/settings")
public class SettingsController extends AbstractResourceController {

    private final SecurityService securityService;
    private final Collection<SettingsManager<?>> settingsManagers;
    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Autowired
    public SettingsController(SecurityService securityService, Collection<SettingsManager<?>> settingsManagers) {
        this.securityService = securityService;
        this.settingsManagers = settingsManagers;
    }

    /**
     * List of forms to configure.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resources<DescribedForm> configuration() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        List<DescribedForm> forms = settingsManagers.stream()
                .sorted((o1, o2) -> o1.getTitle().compareTo(o2.getTitle()))
                .map(this::getSettingsForm)
                .collect(Collectors.toList());
        // OK
        return Resources.of(
                forms,
                uri(on(getClass()).configuration())
        );
    }

    /**
     * Gets settings
     */
    @RequestMapping(value = "/{type:.*}", method = RequestMethod.GET)
    public <T> Resource<T> getSettings(@PathVariable String type) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        T settings = settingsManagers.stream()
                .filter(candidate -> StringUtils.equals(
                        type,
                        getSettingsManagerName(candidate)
                ))
                .map(manager -> (T) manager.getSettings())
                .findFirst()
                .orElse(null);
        if (settings != null) {
            return Resource.of(
                    settings,
                    uri(on(getClass()).getSettings(type))
            );
        } else {
            throw new SettingsManagerNotFoundException(type);
        }
    }


    /**
     * Security
     */
    @RequestMapping(value = "/{type:.*}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public <T> Ack updateSettings(@PathVariable String type, @RequestBody JsonNode settingsNode) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        // Gets the settings manager by type
        @SuppressWarnings("unchecked")
        SettingsManager<T> settingsManager = (SettingsManager<T>) settingsManagers.stream()
                .filter(candidate -> StringUtils.equals(
                        type,
                        getSettingsManagerName(candidate)
                ))
                .findFirst()
                .orElseThrow(() -> new SettingsManagerNotFoundException(type));
        // Parsing
        T settings;
        try {
            settings = objectMapper.treeToValue(settingsNode, settingsManager.getSettingsClass());
        } catch (JsonProcessingException e) {
            throw new SettingsValidationException(e);
        }
        // Saves the settings
        settingsManager.saveSettings(settings);
        // OK
        return Ack.OK;
    }

    private String getSettingsManagerName(SettingsManager<?> settingsManager) {
        return settingsManager.getId();
    }

    private <T> DescribedForm getSettingsForm(SettingsManager<T> settingsManager) {
        return DescribedForm.create(
                getSettingsManagerName(settingsManager),
                settingsManager.getSettingsForm()
        )
                .title(settingsManager.getTitle())
                .uri(uri(on(getClass()).updateSettings(getSettingsManagerName(settingsManager), null)))
                ;
    }

}
