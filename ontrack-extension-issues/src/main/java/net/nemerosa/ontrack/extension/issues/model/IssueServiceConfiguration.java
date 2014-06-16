package net.nemerosa.ontrack.extension.issues.model;

public interface IssueServiceConfiguration {

    String getServiceId();

    String getName();

    default String toId() {
        return String.format("%s//%s", getServiceId(), getName());
    }

}
