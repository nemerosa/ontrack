package net.nemerosa.ontrack.extension.issues.combined;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.extension.issues.model.SelectableIssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiSelection;
import net.nemerosa.ontrack.model.support.Configuration;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.model.form.Form.defaultNameField;

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

    public static Form form(List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations) {
        return new CombinedIssueServiceConfiguration(
                "",
                Collections.emptyList()
        ).asForm(availableIssueServiceConfigurations);
    }

    public Form asForm(List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations) {
        return Form.create()
                .with(defaultNameField())
                .with(
                        MultiSelection.of("issueServiceConfigurationIdentifiers")
                                .label("Issue services")
                                .help("List of issue services to combine.")
                                .items(
                                        availableIssueServiceConfigurations.stream()
                                                .map(
                                                        issueServiceConfigurationRepresentation ->
                                                                new SelectableIssueServiceConfigurationRepresentation(
                                                                        issueServiceConfigurationRepresentation,
                                                                        issueServiceConfigurationIdentifiers.contains(issueServiceConfigurationRepresentation.getId())
                                                                )
                                                )
                                                .collect(Collectors.toList())

                                )
                )
                ;
    }
}
