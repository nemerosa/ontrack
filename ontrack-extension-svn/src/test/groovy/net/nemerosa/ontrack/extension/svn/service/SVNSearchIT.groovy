package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.support.MockIssue
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceExtension
import net.nemerosa.ontrack.extension.issues.support.MockIssueStatus
import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.extension.svn.model.SVNInfoService
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.support.*
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.junit.AfterClass
import org.junit.Before
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
    private SVNInfoService svnInfoService

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

    @Autowired
    private MockIssueServiceExtension mockIssueServiceExtension

    /**
     * Makes sure to reset the list of issues in the mock issue service
     */
    @Before
    void 'Reset issues'() {
        mockIssueServiceExtension.resetIssues()
    }

    @Test
    void '0 - SVN: Search revision'() {
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
            def info = svnInfoService.getOntrackRevisionInfo(svnService.getRepository(configuration.name), 4)
            assert info.revisionInfo.message == "Commit 3 for #2"
            assert info.buildViews.collect { it.build.name } == ['1.0.1']
            assert info.branchStatusViews.size() == 1
            def branchStatusView = info.branchStatusViews[0]
            def promotions = branchStatusView.promotions
            assert promotions.collect { it.promotionLevel.name } == ['COPPER', 'BRONZE']
            assert promotions.collect { it.promotionRun.build.name } == ['1.0.2', '1.0.3']
        }

        asUser().with(branch, ProjectEdit).call {
            def info = svnInfoService.getOntrackRevisionInfo(svnService.getRepository(configuration.name), 8)
            assert info.revisionInfo.message == "Commit 7 for #3"
            assert info.buildViews.collect { it.build.name } == ['1.0.2']
            assert info.branchStatusViews.size() == 1
            def branchStatusView = info.branchStatusViews[0]
            def promotions = branchStatusView.promotions
            assert promotions.collect { it.promotionLevel.name } == ['COPPER', 'BRONZE']
            assert promotions.collect { it.promotionRun.build.name } == ['1.0.2', '1.0.3']
        }

        asUser().with(branch, ProjectEdit).call {
            def info = svnInfoService.getOntrackRevisionInfo(svnService.getRepository(configuration.name), 10)
            assert info.revisionInfo.message == "Commit 9 for #4"
            assert info.buildViews.collect { it.build.name } == ['1.0.3']
            assert info.branchStatusViews.size() == 1
            def branchStatusView = info.branchStatusViews[0]
            def promotions = branchStatusView.promotions
            assert promotions.collect { it.promotionLevel.name } == ['COPPER', 'BRONZE']
            assert promotions.collect { it.promotionRun.build.name } == ['1.0.3', '1.0.3']
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
    void 'SVN: Search linked issues - one branch only'() {
        def testName = 'SVNSearchLinkedIssues'

        /**
         * Initialisation
         */

        init testName
        SVNConfiguration configuration = initConfiguration()
        def branch = configureBranch(configuration, testName)

        /**
         * Issues and links
         */
        def issue1 = new MockIssue(1, MockIssueStatus.OPEN, 'feature')
        def issue2 = new MockIssue(2, MockIssueStatus.OPEN, 'feature')
        def issue3 = new MockIssue(3, MockIssueStatus.OPEN, 'feature')
        def issue4 = new MockIssue(4, MockIssueStatus.OPEN, 'feature')

        issue1.withLinks([issue2, issue3])
        issue2.withLinks([issue1, issue4])
        issue3.withLinks([issue1])
        issue4.withLinks([issue2])

        mockIssueServiceExtension.register(issue1, issue2, issue3, issue4)

        /**
         * Looking for issue #1 must bring the last revision (for issue #4)
         */

        def info = asUser().with(branch, ProjectView).call { svnInfoService.getIssueInfo(configuration.name, '1') }
        assert info.issue.key == '1'
        assert info.issue.displayKey == '#1'

        assert info.revisionInfos.size() == 1
        assert info.revisionInfos.first().revisionInfo.message == 'Commit 10 for #4'

    }

    /**
     * Gets issue info accross two branches by following issue links.
     *
     * <pre>
     *     |  |
     *     *  | (5) Commit #3 (trunk)
     *     |  |
     *     |  * (4) Commit #2 (branches/1.0)
     *     |  |
     *     |  /
     *     | /
     *     * (2) Commit #1
     * </pre>
     *
     * If issues #3 and #2 are linked, lLooking for issue #3 must bring two branches:
     * trunk with revision 5, and branch 1.0 with revision 4
     */
    @Test
    void 'SVN: Search linked issues - links between branches, no merge'() {
        def testName = 'SVNSearchLinkedIssuesMultiBranch'

        /**
         * Initialisation
         */

        repo.mkdir "${testName}/trunk", 'Trunk'
        repo.mkdir "${testName}/trunk/1", 'Commit for #1'
        repo.copy "${testName}/trunk", "${testName}/branches/1.0", "Branch 1.0"
        repo.mkdir "${testName}/branches/1.0/2", 'Commit for #2'
        repo.mkdir "${testName}/trunk/3", 'Commit for #3'

        SVNConfiguration configuration = initConfiguration()

        /**
         * Branches
         */

        // Project
        Project project = doCreateProject()

        // Configuration & branches
        asUser().with(project, ProjectEdit).call {

            propertyService.editProperty(project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    "/${testName}/trunk"
            ))

            // Trunk configuration
            def trunk = doCreateBranch(project, nd('trunk', ''))
            propertyService.editProperty(trunk, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    "/${testName}/trunk",
                    TagNameSvnRevisionLink.DEFAULT
            ))

            // Branch configuration
            def branch10 = doCreateBranch(project, nd('1.0', ''))
            propertyService.editProperty(branch10, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    "/${testName}/branches/1.0",
                    TagNameSvnRevisionLink.DEFAULT
            ))

        }

        /**
         * Issues and links
         */
        def issue1 = new MockIssue(1, MockIssueStatus.OPEN, 'feature')
        def issue2 = new MockIssue(2, MockIssueStatus.OPEN, 'feature')
        def issue3 = new MockIssue(3, MockIssueStatus.OPEN, 'feature')

        issue2.withLinks([issue3])
        issue3.withLinks([issue2])

        mockIssueServiceExtension.register(issue1, issue2, issue3)

        /**
         * Search
         */

        def info = asUser().with(project, ProjectView).call { svnInfoService.getIssueInfo(configuration.name, '3') }
        assert info.issue.key == '3'
        assert info.issue.displayKey == '#3'

        assert info.revisionInfos.size() == 2
        assert info.revisionInfos.first().revisionInfo.message == 'Commit for #3'
        assert info.revisionInfos.last().revisionInfo.message == 'Commit for #2'

    }

    protected static void init(String test) {
        repo.mkdir "${test}/trunk", 'Trunk'
        (1..10).each {
            def key = ((it / 3) + 1) as int // 1..4
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
                    TagNameSvnRevisionLink.DEFAULT
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
        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration.withIssueServiceConfigurationIdentifier(issueServiceIdentifier)
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
