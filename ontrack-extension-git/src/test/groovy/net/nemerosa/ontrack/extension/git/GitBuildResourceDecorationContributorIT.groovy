package net.nemerosa.ontrack.extension.git

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.git.support.GitRepo
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.it.ResourceDecorationContributorTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.ui.resource.ResourceModule
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapper
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid

class GitBuildResourceDecorationContributorIT extends AbstractServiceTestSupport {

    @Autowired
    private PropertyService propertyService

    @Autowired
    private GitConfigurationService gitConfigurationService

    @Autowired
    private SecurityService securityService

    @Autowired
    private ResourceModule resourceModule

    @Autowired
    private GitBuildResourceDecorationContributor contributor

    private ResourceObjectMapper resourceObjectMapper

    @Before
    void 'Setup'() {
        resourceObjectMapper = ResourceDecorationContributorTestSupport.createResourceObjectMapper(
                Build,
                contributor,
                securityService
        )
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
        def repo = new GitRepo()
        try {
            // Creates a Git repository with some commits
            repo.with {
                git 'init'
                (1..4).each { commit it }
            }

            // Create a Git configuration
            String gitConfigurationName = uid('C')
            BasicGitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
                gitConfigurationService.newConfiguration(
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withRemote("file://${repo.dir.absolutePath}")
                )
            }

            // Creates a build
            def build = doCreateBuild()

            // Configures the project
            asUser().with(build, ProjectConfig).call {
                propertyService.editProperty(
                        build.project,
                        GitProjectConfigurationPropertyType,
                        new GitProjectConfigurationProperty(gitConfiguration)
                )
                // ...  & the branch with a link based on commits
                propertyService.editProperty(
                        build.branch,
                        GitBranchConfigurationPropertyType,
                        new GitBranchConfigurationProperty(
                                'master',
                                new ConfiguredBuildGitCommitLink<>(
                                        new CommitBuildNameGitCommitLink(),
                                        new CommitLinkConfig(true)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
            }

            JsonNode node = resourceObjectMapper.objectMapper.valueToTree(build)

            println resourceObjectMapper.objectMapper.writeValueAsString(build)
            assert node.get("_changeLog").asText() == "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLog:BuildDiffRequest%28from%3D${build.id}%2C+to%3Dnull%29" as String
            assert node.get("_changeLogPage").asText() == "urn:test:#:extension/git/changelog"
        } finally {
            repo.close()
        }
    }
}
