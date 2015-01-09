package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration

class MockIssueServiceConfiguration implements IssueServiceConfiguration {

    public static final MockIssueServiceConfiguration INSTANCE = new MockIssueServiceConfiguration('default')

    private final String name

    MockIssueServiceConfiguration(String name) {
        this.name = name
    }

    @Override
    String getServiceId() {
        'mock'
    }

    @Override
    String getName() {
        name
    }
}
