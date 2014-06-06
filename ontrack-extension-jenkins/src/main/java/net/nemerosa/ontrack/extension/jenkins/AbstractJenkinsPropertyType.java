package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;

public abstract class AbstractJenkinsPropertyType<T extends AbstractJenkinsProperty> extends AbstractPropertyType<T> {

    protected final JenkinsConfigurationService configurationService;

    protected AbstractJenkinsPropertyType(JenkinsConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public Form getEditionForm(T value) {
        return Form.create()
                .with(
                        Selection.of("configuration")
                                .label("Configuration")
                                .help("Jenkins configuration to use when connecting")
                                .items(configurationService.getConfigurationDescriptors())
                                .value(value != null ? value.getConfiguration().getName() : null)
                );
    }


    protected JenkinsConfiguration loadConfiguration(String configurationName) {
        return configurationService.getObfuscatedConfiguration(configurationName);
    }
}
