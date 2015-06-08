package net.nemerosa.ontrack.extension.issues.combined;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.model.support.Configuration;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

import java.util.List;

@Data
public class CombinedIssueServiceConfiguration implements Configuration, IssueServiceConfiguration {

    private final String name;
    private final List<String> issueServiceConfigurationIdentifiers;

    @Override
    public String getServiceId() {
        return CombinedIssueServiceExtension.SERVICE;
    }

    @Override
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor(name, name);
    }

    @Override
    public Configuration obfuscate() {
        return this;
    }
}
