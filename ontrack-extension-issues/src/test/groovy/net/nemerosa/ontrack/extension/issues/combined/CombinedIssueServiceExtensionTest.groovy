package net.nemerosa.ontrack.extension.issues.combined

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class CombinedIssueServiceExtensionTest {

    private CombinedIssueServiceExtension service
    private CombinedIssueServiceConfiguration configuration
    private IssueServiceExtension type1IssueService
    private IssueServiceExtension type2IssueService
    private MockIssueServiceConfiguration testConfiguration

    @Before
    void 'Setup'() {
        IssueServiceRegistry issueServiceRegistry = mock(IssueServiceRegistry)
        IssueExportServiceFactory issueExportServiceFactory = mock(IssueExportServiceFactory)
        service = new CombinedIssueServiceExtension(
                new CombinedIssueServiceExtensionFeature(),
                issueServiceRegistry,
                issueExportServiceFactory
        )

        configuration = new CombinedIssueServiceConfiguration(
                'test',
                ['type1:test', 'type2:test']
        )

        type1IssueService = mock(IssueServiceExtension)

        type2IssueService = mock(IssueServiceExtension)


        testConfiguration = new MockIssueServiceConfiguration('test')

        when(issueServiceRegistry.getConfiguredIssueService('type1:test')).thenReturn(
                new ConfiguredIssueService(
                        type1IssueService,
                        testConfiguration
                )
        )

        when(issueServiceRegistry.getConfiguredIssueService('type2:test')).thenReturn(
                new ConfiguredIssueService(
                        type2IssueService,
                        testConfiguration
                )
        )
    }

    @Test
    void 'Extraction of issue keys'() {
        def message = 'ONTRACK-1 #1 A message'

        when(type1IssueService.extractIssueKeysFromMessage(testConfiguration, message)).thenReturn(['ONTRACK-1'] as Set)
        when(type2IssueService.extractIssueKeysFromMessage(testConfiguration, message)).thenReturn(['1'] as Set)

        def keys = service.extractIssueKeysFromMessage(configuration, message)
        assert keys == ['ONTRACK-1', '1'] as Set
    }

    @Test
    void 'No issue found'() {
        assert service.getIssue(configuration, '1') == null
    }

    @Test
    void 'One issue found - 1'() {
        Issue issue1 = mock(Issue)
        when(type1IssueService.getIssue(testConfiguration, '1')).thenReturn(issue1)
        assert service.getIssue(configuration, '1') == issue1
    }

    @Test
    void 'One issue found - 2'() {
        Issue issue2 = mock(Issue)
        when(type2IssueService.getIssue(testConfiguration, '1')).thenReturn(issue2)
        assert service.getIssue(configuration, '1') == issue2
    }

    @Test
    void 'Two issues found - takes the first one'() {
        Issue issue1 = mock(Issue)
        Issue issue2 = mock(Issue)
        when(type1IssueService.getIssue(testConfiguration, '1')).thenReturn(issue1)
        when(type2IssueService.getIssue(testConfiguration, '1')).thenReturn(issue2)
        assert service.getIssue(configuration, '1') == issue1
    }

}
