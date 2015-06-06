package net.nemerosa.ontrack.extension.issues.combined;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;

@Data
public class CombinedIssueServiceConfiguration implements IssueServiceConfiguration {

    private final String name;

    @Override
    public String getServiceId() {
        return CombinedIssueServiceExtension.SERVICE;
    }

}
