package net.nemerosa.ontrack.extension.issues.model;

public interface IssueServiceConfiguration {

    String getServiceId();

    String getName();

    default IssueServiceConfigurationIdentifier toIdentifier() {
        return new IssueServiceConfigurationIdentifier(
                getServiceId(),
                getName()
        );
    }

}
