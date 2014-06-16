package net.nemerosa.ontrack.extension.issues.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueServiceConfigurationId {

    private final String id;
    private final String name;

    public static IssueServiceConfigurationId of(IssueServiceExtension issueServiceExtension, IssueServiceConfiguration issueServiceConfiguration) {
        return new IssueServiceConfigurationId(
                String.format("%s//%s", issueServiceExtension.getId(), issueServiceConfiguration.getName()),
                String.format("%s (%s)", issueServiceConfiguration.getName(), issueServiceExtension.getName())
        );
    }
}
