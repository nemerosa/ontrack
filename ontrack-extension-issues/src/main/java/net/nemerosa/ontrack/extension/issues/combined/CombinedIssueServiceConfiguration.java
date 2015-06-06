package net.nemerosa.ontrack.extension.issues.combined;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;

import java.util.List;

@Data
public class CombinedIssueServiceConfiguration implements IssueServiceConfiguration {

    private final String name;
    private final List<String> issueServiceConfigurationIdentifiers;

    @Override
    public String getServiceId() {
        return CombinedIssueServiceExtension.SERVICE;
    }

}
