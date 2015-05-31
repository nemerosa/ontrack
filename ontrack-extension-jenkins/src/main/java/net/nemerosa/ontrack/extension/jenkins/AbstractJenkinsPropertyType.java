package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.function.Function;

public abstract class AbstractJenkinsPropertyType<T extends AbstractJenkinsProperty> extends AbstractPropertyType<T> {

    protected final JenkinsConfigurationService configurationService;

    protected AbstractJenkinsPropertyType(JenkinsConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, T value) {
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
        return configurationService.getConfiguration(configurationName);
    }

    protected JenkinsConfiguration replaceConfiguration(JenkinsConfiguration configuration, Function<String, String> replacementFunction) {
        return configurationService.replaceConfiguration(configuration, replacementFunction);
    }
}
