package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import org.mockito.Mockito

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

    static ConfiguredIssueService configuredIssueService(String name) {
        return new ConfiguredIssueService(
                new MockIssueServiceExtension(
                        new MockIssueServiceFeature(),
                        Mockito.mock(IssueExportServiceFactory)
                ),
                new MockIssueServiceConfiguration(name)
        )
    }
}
