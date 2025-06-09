package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType;

import java.util.function.Function;

public abstract class AbstractJenkinsPropertyType<T extends AbstractJenkinsProperty> extends AbstractPropertyType<T>
        implements ConfigurationPropertyType<JenkinsConfiguration, T> {

    protected final JenkinsConfigurationService configurationService;

    protected AbstractJenkinsPropertyType(JenkinsExtensionFeature extensionFeature, JenkinsConfigurationService configurationService) {
        super(extensionFeature);
        this.configurationService = configurationService;
    }

    protected JenkinsConfiguration loadConfiguration(String configurationName) {
        return configurationService.getConfiguration(configurationName);
    }

    protected JenkinsConfiguration replaceConfiguration(JenkinsConfiguration configuration, Function<String, String> replacementFunction) {
        return configurationService.replaceConfiguration(configuration, replacementFunction);
    }
}
