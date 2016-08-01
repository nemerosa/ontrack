package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.extension.svn.model.SVNInfoService
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.extension.svn.support.SVNProfileValueSource
import net.nemerosa.ontrack.extension.svn.support.SVNTestRepo
import net.nemerosa.ontrack.extension.svn.support.SVNTestUtils
import net.nemerosa.ontrack.extension.svn.support.TagNameSvnRevisionLink
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.apache.commons.io.FileUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.ProfileValueSourceConfiguration

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Integration tests for the search and collection of issues and revisions.
 */
@ProfileValueSourceConfiguration(SVNProfileValueSource)
class SVNSearchMultiBranchIT extends AbstractServiceTestSupport {

    private static SVNTestRepo repo

    @BeforeClass
    static void 'SVN repository: start'() {
        repo = SVNTestRepo.get('SVNSearchMultiBranchIT')
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

    /**
     * Anonymous merge.
     *
     * Following structure:
     *
     * <pre>
     *   * Commit 5 (tag: v1.3.0)
     *   * Merge
     *   |\
     *   | * Commit 4 for #1 (tag: v1.2.1)
     *   | * Commit 3 for #1
     *   |/
     *   * Commit 2 (tag: v1.2.0)
     *   * Commit 1
     * </pre>
     */
    @Test
    void 'SVN: Search issue on several branches, with anonymous merge'() {
        def testName = 'SVNSearchIssueAnonymousMerge'

        /**
         * Initialisation
         */

        // Initial trunk
        repo.mkdir "${testName}/trunk", 'Trunk' // 1
        repo.mkdir "${testName}/trunk/1", 'Commit 1' // 2
        repo.mkdir "${testName}/trunk/2", 'Commit 2' // 3
        repo.copy "${testName}/trunk", "${testName}/tags/v1.2.0", 'v1.2.0' // 4

        // Feature branch
        int revision = repo.copy "${testName}/trunk", "${testName}/branches/1.2", 'Feature 1' // 5
        repo.mkdir "${testName}/branches/1.2/3", 'Commit 3 for #1' // 6
        repo.mkdir "${testName}/branches/1.2/4", 'Commit 4 for #1' // 7
        repo.copy "${testName}/branches/1.2", "${testName}/tags/v1.2.1", 'v1.2.1' // 8

        // Merge
        repo.merge "${testName}/branches/1.2@${revision}", "${testName}/trunk", 'Merge' // 9

        // Last commit on trunk
        repo.mkdir "${testName}/trunk/5", 'Commit 5' // 10
        repo.copy "${testName}/trunk", "${testName}/tags/v1.3.0", 'v1.3.0' // 11

        // Saves the configuration
        SVNConfiguration configuration = initConfiguration()

        /**
         * Project
         */

        Project project = doCreateProject()
        Branch branch12, branch13
        asUser().with(project, ProjectEdit).call {
            // Project's configuration
            propertyService.editProperty(project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    "/${testName}/trunk"
            ))
            // Creates and configures two branches
            branch12 = configureBranch(project, testName, 'branches/1.2')
            branch13 = configureBranch(project, testName, 'trunk')
            // Builds for the tags
            build(branch12, 'v1.2.0', 'COPPER')
            build(branch12, 'v1.2.1', 'COPPER', 'BRONZE')
            build(branch13, 'v1.3.0', 'COPPER', 'BRONZE')
        }

        /**
         * Search for issue #1
         */

        def info = asUser().with(project, ProjectEdit).call { svnInfoService.getIssueInfo(configuration.name, '1') }
        assert info.issue.displayKey == '#1'

        // Last revisions on the branches
        assert info.revisionInfos.collect { it.revisionInfo.revision } == [7, 9]
        assert info.revisionInfos.collect { it.branchInfos.branch.name } == [[branch12.name], [branch13.name]]
        assert info.revisionInfos.collect { it.branchInfos.buildView.build.name } == [['v1.2.1'], ['v1.3.0']]

        // Checks earliest promotions for each branch
        // Rev. 7 --> 1.2
        def promotions = info.revisionInfos[0].branchInfos.branchStatusView.promotions
        assert promotions.collect { it.promotionLevel.name } == [['COPPER', 'BRONZE']]
        assert promotions.collect { it.promotionRun.build.name } == [['v1.2.1', 'v1.2.1']]
        // Rev. 9 --> 1.3
        promotions = info.revisionInfos[1].branchInfos.branchStatusView.promotions
        assert promotions.collect { it.promotionLevel.name } == [['COPPER', 'BRONZE']]
        assert promotions.collect { it.promotionRun.build.name } == [['v1.3.0', 'v1.3.0']]

    }

    protected void build(Branch branch, String name, String... promotions) {
        asUser().with(branch, ProjectEdit).call {
            def build = structureService.newBuild(
                    Build.of(
                            branch,
                            nd(name, 'Build'),
                            securityService.currentSignature
                    )
            )
            promotions.each { promotion ->
                def promotionLevel = structureService.findPromotionLevelByName(branch.project.name, branch.name, promotion).get()
                structureService.newPromotionRun(
                        PromotionRun.of(
                                build,
                                promotionLevel,
                                securityService.currentSignature,
                                ''
                        )
                )
            }
        }
    }

    protected Branch configureBranch(Project project, String testName, String path) {
        Branch branch = doCreateBranch(project, nd(uid('B'), "Branch"))
        asUser().with(branch, ProjectEdit).call {
            // Branch's configuration
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    "/${testName}/${path}",
                    TagNameSvnRevisionLink.DEFAULT
            ))
            // Two promotion levels
            structureService.newPromotionLevel(PromotionLevel.of(branch, nd('COPPER', 'Copper promotion')))
            structureService.newPromotionLevel(PromotionLevel.of(branch, nd('BRONZE', 'Bronze promotion')))
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
