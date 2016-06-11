package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.support.*
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.ProfileValueSourceConfiguration

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

@ProfileValueSourceConfiguration(SVNProfileValueSource)
class SVNChangeLogIT extends AbstractServiceTestSupport {

    private SVNTestRepo repo

    @Before
    void 'SVN repository: start'() {
        repo = SVNTestRepo.get('SVNChangeLogIT')
    }

    @After
    void 'SVN repository: stop'() {
        repo.stop()
    }

    @Autowired
    private SVNChangeLogService svnChangeLogService

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

    @Autowired
    private RevisionPatternSvnRevisionLink revisionPatternSvnRevisionLink


    @Test
    void 'SVN Change log without issues'() {

        /**
         * Preparation of a repository with a few commits on the trunk
         */

        repo.mkdir 'SVNChangeLogWithoutIssues/trunk', 'Trunk'
        (1..3).each { repo.mkdir "SVNChangeLogWithoutIssues/trunk/$it", "$it" }
        repo.copy 'SVNChangeLogWithoutIssues/trunk', 'SVNChangeLogWithoutIssues/tags/v1', 'Tag v1'
        (4..6).each { repo.mkdir "SVNChangeLogWithoutIssues/trunk/$it", "$it" }
        repo.copy 'SVNChangeLogWithoutIssues/trunk', 'SVNChangeLogWithoutIssues/tags/v2', 'Tag v2'
        (7..9).each { repo.mkdir "SVNChangeLogWithoutIssues/trunk/$it", "$it" }
        repo.copy 'SVNChangeLogWithoutIssues/trunk', 'SVNChangeLogWithoutIssues/tags/v3', 'Tag v3'

        /**
         * Definition of the repository
         */

        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration
        def repositoryId = repositoryDao.getOrCreateByName(configuration.name)
        def repository = SVNRepository.of(repositoryId, configuration, null)

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
         * Branch with this configuration
         */

        Branch branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    '/SVNChangeLogWithoutIssues/trunk'
            ))
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    '/SVNChangeLogWithoutIssues/trunk',
                    TagNameSvnRevisionLink.DEFAULT
            ))
        }

        /**
         * Builds for the tags
         */

        def build1 = doCreateBuild(branch, nd("v1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("v2", "Build 2"))
        doCreateBuild(branch, nd("v3", "Build 3"))

        /**
         * Change log
         */

        def diff = new BuildDiffRequest()
        diff.from = build1.id
        diff.to = build2.id
        def changeLog = asUser().with(branch, ProjectView).call {
            svnChangeLogService.changeLog(diff)
        }

        /**
         * Checks the change log
         */

        assert changeLog.branch == branch
        assert changeLog.from.build == build1
        assert changeLog.to.build == build2

        /**
         * Gets the revisions
         */

        def changeLogRevisions = svnChangeLogService.getChangeLogRevisions(changeLog)
        assert changeLogRevisions.list.collect { it.message } == ['6', '5', '4']

    }

    @Test
    void 'SVN Change log with revision pattern'() {

        /**
         * Preparation of a repository with a few commits on the trunk
         */

        repo.mkdir 'SVNChangeLogWithRevisionPattern/trunk', 'Trunk'
        (1..10).each { repo.mkdir "SVNChangeLogWithRevisionPattern/trunk/$it", "$it" }

        /**
         * Definition of the repository
         */

        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration
        def repositoryId = repositoryDao.getOrCreateByName(configuration.name)
        def repository = SVNRepository.of(repositoryId, configuration, null)

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
         * Branch with this configuration
         */

        Branch branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    '/SVNChangeLogWithRevisionPattern/trunk'
            ))
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    '/SVNChangeLogWithRevisionPattern/trunk',
                    new ConfiguredBuildSvnRevisionLink<>(
                            revisionPatternSvnRevisionLink,
                            new RevisionPattern("1.0.*-{revision}")
                    ).toServiceConfiguration()
            ))
        }

        /**
         * Builds for some revisions
         */

        def build1 = doCreateBuild(branch, nd("1.0.0-5", "Build 5"))
        def build2 = doCreateBuild(branch, nd("1.0.1-9", "Build 9"))

        /**
         * Change log
         */

        def diff = new BuildDiffRequest()
        diff.from = build1.id
        diff.to = build2.id
        def changeLog = asUser().with(branch, ProjectView).call {
            svnChangeLogService.changeLog(diff)
        }

        /**
         * Checks the change log
         */

        assert changeLog.branch == branch
        assert changeLog.from.build == build1
        assert changeLog.to.build == build2

        /**
         * Gets the revisions
         */

        def changeLogRevisions = svnChangeLogService.getChangeLogRevisions(changeLog)
        assert changeLogRevisions.list.collect { it.revision } as long[] == [9, 8, 7, 6] as long[]

    }


    @Test
    void 'SVN Change log with issues'() {

        /**
         * Preparation of a repository with a few commits on the trunk
         */

        repo.mkdir 'SVNChangeLogWithIssues/trunk', 'Trunk'
        (1..3).each { repo.mkdir "SVNChangeLogWithIssues/trunk/$it", "$it" }
        repo.copy 'SVNChangeLogWithIssues/trunk', 'SVNChangeLogWithIssues/tags/v1', 'Tag v1'
        (4..6).each { repo.mkdir "SVNChangeLogWithIssues/trunk/$it", "Commit for issue #$it" }
        repo.copy 'SVNChangeLogWithIssues/trunk', 'SVNChangeLogWithIssues/tags/v2', 'Tag v2'
        (7..9).each { repo.mkdir "SVNChangeLogWithIssues/trunk/$it", "$it" }
        repo.copy 'SVNChangeLogWithIssues/trunk', 'SVNChangeLogWithIssues/tags/v3', 'Tag v3'

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
         * Branch with this configuration
         */

        Branch branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    '/SVNChangeLogWithIssues/trunk'
            ))
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    '/SVNChangeLogWithIssues/trunk',
                    TagNameSvnRevisionLink.DEFAULT
            ))
        }

        /**
         * Builds for the tags
         */

        def build1 = doCreateBuild(branch, nd("v1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("v2", "Build 2"))
        doCreateBuild(branch, nd("v3", "Build 3"))

        /**
         * Change log
         */

        def diff = new BuildDiffRequest()
        diff.from = build1.id
        diff.to = build2.id
        def changeLog = asUser().with(branch, ProjectView).call {
            svnChangeLogService.changeLog(diff)
        }

        /**
         * Checks the change log
         */

        assert changeLog.branch == branch
        assert changeLog.from.build == build1
        assert changeLog.to.build == build2

        /**
         * Gets the revisions
         */

        def changeLogRevisions = svnChangeLogService.getChangeLogRevisions(changeLog)
        assert changeLogRevisions.list.collect { it.message } == (6..4).collect { "Commit for issue #${it}" }

        /**
         * Gets the issues
         */

        def changeLogIssues = svnChangeLogService.getChangeLogIssues(changeLog)
        assert changeLogIssues.list.collect { it.issue.key } == (4..6).collect { it as String }

    }
}
