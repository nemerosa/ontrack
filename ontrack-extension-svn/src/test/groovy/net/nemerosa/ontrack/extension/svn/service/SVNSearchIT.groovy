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
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.ProfileValueSourceConfiguration

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

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
    private StructureService structureService

    @Autowired
    private SecurityService securityService

    @Autowired
    private SVNConfigurationService svnConfigurationService

    @Autowired
    private IssueServiceRegistry issueServiceRegistry

    @Autowired
    private SVNRepositoryDao repositoryDao

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

        // Creates some tags
        repo.copy "${testName}/trunk@5", "${testName}/tags/1.0.1", 'v1.0.1'
        repo.copy "${testName}/trunk@8", "${testName}/tags/1.0.2", 'v1.0.2'
        repo.copy "${testName}/trunk@11", "${testName}/tags/1.0.3", 'v1.0.3'

        // Saves the configuration
        SVNConfiguration configuration = initConfiguration()
        def branch = configureBranch(configuration, testName)

        /**
         * Promotions
         */

        asUser().with(branch, ProjectEdit).call {
            def info = svnService.getOntrackRevisionInfo(svnService.getRepository(configuration.name), 4)
            assert info.revisionInfo.message == "Commit 3 for #2"
            assert info.buildViews.collect { it.build.name } == ['1.0.1']
            assert info.branchStatusViews.size() == 1
            def branchStatusView = info.branchStatusViews[0]
            def promotions = branchStatusView.promotions
            assert promotions.collect { it.promotionLevel.name } == ['COPPER', 'BRONZE']
            assert promotions.collect { it.promotionRun.build.name } == ['1.0.2', '1.0.3']
        }

        asUser().with(branch, ProjectEdit).call {
            def info = svnService.getOntrackRevisionInfo(svnService.getRepository(configuration.name), 8)
            assert info.revisionInfo.message == "Commit 7 for #3"
            assert info.buildViews.collect { it.build.name } == ['1.0.2']
            assert info.branchStatusViews.size() == 1
            def branchStatusView = info.branchStatusViews[0]
            def promotions = branchStatusView.promotions
            assert promotions.collect { it.promotionLevel.name } == ['COPPER', 'BRONZE']
            assert promotions.collect { it.promotionRun.build.name } == ['1.0.2', '1.0.3']
        }

        asUser().with(branch, ProjectEdit).call {
            def info = svnService.getOntrackRevisionInfo(svnService.getRepository(configuration.name), 10)
            assert info.revisionInfo.message == "Commit 9 for #4"
            assert info.buildViews.collect { it.build.name } == ['1.0.3']
            assert info.branchStatusViews.size() == 1
            def branchStatusView = info.branchStatusViews[0]
            def promotions = branchStatusView.promotions
            assert promotions.collect { it.promotionLevel.name } == ['COPPER', 'BRONZE']
            assert promotions.collect { it.promotionRun.build.name } == ['1.0.3', '1.0.3']
        }

    }

    protected static void init(String test) {
        repo.mkdir "${test}/trunk", 'Trunk'
        (1..10).each {
            def key = ((it / 3) + 1) as int
            def message = "Commit $it for #$key"
            repo.mkdir "${test}/trunk/${it}", message // Revision = it + 1
        }
    }

    protected Branch configureBranch(SVNConfiguration configuration, String testName) {
        Branch branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            // Project's configuration
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    "/${testName}/trunk"
            ))
            // Branch's configuration
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    "/${testName}/trunk",
                    "/${testName}/tags/{build}"
            ))
            // Two promotion levels
            PromotionLevel copper = structureService.newPromotionLevel(PromotionLevel.of(branch, nd('COPPER', 'Copper promotion')))
            PromotionLevel bronze = structureService.newPromotionLevel(PromotionLevel.of(branch, nd('BRONZE', 'Bronze promotion')))
            // Builds
            structureService.newBuild(Build.of(branch, nd('1.0.1', 'Build 1'), securityService.currentSignature))
            def build2 = structureService.newBuild(Build.of(branch, nd('1.0.2', 'Build 2'), securityService.currentSignature))
            def build3 = structureService.newBuild(Build.of(branch, nd('1.0.3', 'Build 3'), securityService.currentSignature))
            // Promotions
            structureService.newPromotionRun(PromotionRun.of(
                    build2,
                    copper,
                    securityService.currentSignature,
                    'Promotion of 1.0.2 to Copper'
            ))
            structureService.newPromotionRun(PromotionRun.of(
                    build3,
                    copper,
                    securityService.currentSignature,
                    'Promotion of 1.0.3 to Copper'
            ))
            structureService.newPromotionRun(PromotionRun.of(
                    build3,
                    bronze,
                    securityService.currentSignature,
                    'Promotion of 1.0.3 to Bronze'
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
