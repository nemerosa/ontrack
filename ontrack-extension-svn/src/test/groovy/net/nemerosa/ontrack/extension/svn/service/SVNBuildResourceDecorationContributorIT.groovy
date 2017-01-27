package net.nemerosa.ontrack.extension.svn.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.svn.SVNBuildResourceDecorationContributor
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.support.SVNTestRepo
import net.nemerosa.ontrack.extension.svn.support.SVNTestUtils
import net.nemerosa.ontrack.extension.svn.support.TagNameSvnRevisionLink
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.it.ResourceDecorationContributorTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SVNBuildResourceDecorationContributorIT extends AbstractServiceTestSupport {

    @Autowired
    private SVNService svnService

    @Autowired
    private SVNBuildResourceDecorationContributor contributor

    @Autowired
    private SecurityService securityService

    @Autowired
    private SVNRepositoryDao repositoryDao

    @Autowired
    private SVNConfigurationService svnConfigurationService

    @Autowired
    private PropertyService propertyService

    private ResourceObjectMapper resourceObjectMapper

    private SVNTestRepo repo

    @Before
    void 'SVN repository: start'() {
        repo = SVNTestRepo.get('SVNChangeLogIT')
        resourceObjectMapper = ResourceDecorationContributorTestSupport.createResourceObjectMapper(
                Build,
                contributor,
                securityService
        )
    }

    @After
    void 'SVN repository: stop'() {
        repo.stop()
    }

    @Test
    void 'No change log link on a build not configured'() {
        // Creates a build
        def build = doCreateBuild()

        JsonNode node = resourceObjectMapper.objectMapper.valueToTree(build)
        assert node.get("_changeLog") == null
        assert node.get("_changeLogPage") == null
    }

    @Test
    void 'Change log link on a build'() {

        /**
         * Preparation of a repository with a few commits on the trunk
         */

        repo.mkdir 'SVNBuildResourceDecorationContributorIT/trunk', 'Trunk'
        (1..3).each { repo.mkdir "SVNBuildResourceDecorationContributorIT/trunk/$it", "$it" }

        /**
         * Definition of the repository
         */

        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration
        repositoryDao.getOrCreateByName(configuration.name)

        /**
         * Saves the configuration
         */

        asUser().with(GlobalSettings).call {
            configuration = svnConfigurationService.newConfiguration(configuration)
        }

        /**
         * Branch with this configuration
         */

        Branch branch = doCreateBranch()
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(branch.project, SVNProjectConfigurationPropertyType, new SVNProjectConfigurationProperty(
                    configuration,
                    '/SVNBuildResourceDecorationContributorIT/trunk'
            ))
            propertyService.editProperty(branch, SVNBranchConfigurationPropertyType, new SVNBranchConfigurationProperty(
                    '/SVNBuildResourceDecorationContributorIT/trunk',
                    TagNameSvnRevisionLink.DEFAULT
            ))
        }

        // Creates a build
        def build = doCreateBuild(branch, NameDescription.nd('1', ''))

        JsonNode node = resourceObjectMapper.objectMapper.valueToTree(build)

        println resourceObjectMapper.objectMapper.writeValueAsString(build)
        assert node.get("_changeLog").asText() == "urn:test:net.nemerosa.ontrack.extension.svn.SVNController#changeLog:BuildDiffRequest%28from%3D${build.id}%2C+to%3Dnull%29" as String
        assert node.get("_changeLogPage").asText() == "urn:test:#:extension/svn/changelog"
    }
}
