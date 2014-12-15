package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.extension.svn.support.SVNProfileValueSource
import net.nemerosa.ontrack.extension.svn.support.SVNTestRepo
import net.nemerosa.ontrack.extension.svn.support.SVNTestUtils
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.ProfileValueSourceConfiguration

/**
 * Integration tests for the search and collection of issues and revisions.
 */
@ProfileValueSourceConfiguration(SVNProfileValueSource)
class SVNSearchIT extends AbstractServiceTestSupport {

    private static SVNTestRepo repo

    @BeforeClass
    static void 'SVN repository: start'() {
        repo = SVNTestRepo.get('SVNSearchIT')
    }

    @AfterClass
    static void 'SVN repository: stop'() {
        repo.stop()
    }

    @Autowired
    private SVNService svnService

    @Autowired
    private IndexationService indexationService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private SVNConfigurationService svnConfigurationService

    @Autowired
    private IssueServiceRegistry issueServiceRegistry

    @Autowired
    private SVNRepositoryDao repositoryDao

    protected static void init(String test) {
        repo.mkdir "${test}/trunk", 'Trunk'
        (1..10).each {
            def key = ((it / 3) + 1) as int
            def message = "Commit $it for #$key"
            repo.mkdir "${test}/trunk/${it}", message // Revision = it + 1
        }
    }

    @Test
    void 'SVN: Search issue'() {
        def testName = 'SVNSearchIssue'

        /**
         * Initialisation
         */

        init testName
        SVNConfiguration configuration = initConfiguration()
        configureBranch(configuration, testName)

        /**
         * Looking for an existing issue
         */

        (1..4).each {
            def optIssue = svnService.searchIssues(svnService.getRepository(configuration.name), "#${it}")
            assert optIssue.present
            def issue = optIssue.get()
            assert issue.issue.key == "${it}"
            assert issue.issue.displayKey == "#${it}"
            assert issue.repository.configuration.name == configuration.name
        }

    }

    @Test
    void 'SVN: Search revision'() {
        def testName = 'SVNSearchRevision'

        /**
         * Initialisation
         */

        init testName
        SVNConfiguration configuration = initConfiguration()
        configureBranch(configuration, testName)

        /**
         * Looking for commits
         */

        (2..11).each { revision ->
            def key = (((revision - 1) / 3) + 1) as int
            def revisionInfo = svnService.getOntrackRevisionInfo(svnService.getRepository(configuration.name), revision)
            assert revisionInfo.revisionInfo.message == "Commit ${revision - 1} for #${key}"
        }

    }

    protected Branch configureBranch(SVNConfiguration configuration, String testName) {
        Branch branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    "/${testName}/trunk"
            ))
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    "/${testName}/trunk",
                    "/${testName}/tags/{build}"
            ))
        }
        branch
    }

    protected SVNConfiguration initConfiguration() {
        /**
         * Definition of the repository
         */

        def issueServiceIdentifier = MockIssueServiceConfiguration.INSTANCE.toIdentifier().format()
        def configuration = SVNTestUtils.repository().configuration.withIssueServiceConfigurationIdentifier(issueServiceIdentifier)
        def repositoryId = repositoryDao.getOrCreateByName(configuration.name)
        def repository = SVNRepository.of(
                repositoryId,
                configuration,
                issueServiceRegistry.getConfiguredIssueService(issueServiceIdentifier)
        )

        /**
         * Saves the configuration
         */

        asUser().with(GlobalSettings).call {
            configuration = svnConfigurationService.newConfiguration(configuration)
        }

        /**
         * Indexation of this repository
         */

        asUser().with(GlobalSettings).call {
            ((IndexationServiceImpl) indexationService).indexFromLatest(repository, { println it })
        }

        /**
         * Configuration
         */
        configuration
    }
}
