package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;

import java.util.Optional;

public abstract class AbstractJenkinsPropertyType<T extends AbstractJenkinsProperty> extends AbstractPropertyType<T> {

    protected final JenkinsConfigurationService configurationService;

    protected AbstractJenkinsPropertyType(JenkinsConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public Form getEditionForm(Optional<T> value) {
        return Form.create()
                .with(
                        Selection.of("configuration")
                                .label("Configuration")
                                .help("Jenkins configuration to use when connecting")
                                .items(configurationService.getConfigurations())
                                .itemId("name")
                );
    }
}
