package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class RevisionPatternSvnRevisionLinkTemplateIT extends AbstractServiceTestSupport {

    @Autowired
    private PropertyService propertyService

    @Autowired
    private BranchTemplateService branchTemplateService

    @Autowired
    private SVNConfigurationService svnConfigurationService

    @Autowired
    private RevisionPatternSvnRevisionLink link

    private SVNTestRepo repo

    @Before
    void 'SVN repository: start'() {
        repo = SVNTestRepo.get('RevisionPatternSvnRevisionLinkTemplateIT')
    }

    @After
    void 'SVN repository: stop'() {
        repo.stop()
    }

    @Test
    void 'Templating with configurable revision pattern'() {
        // Sample configuration
        def configuration = SVNTestUtils.repository(repo.url.toString()).configuration
        asUser().with(GlobalSettings).call {
            configuration = svnConfigurationService.newConfiguration(configuration)
        }
        // Creating a branch template
        def branch = doCreateBranch()
        // SVN property on the project
        asUser().with(branch, ProjectEdit).call {
            propertyService.editProperty(
                    branch.project,
                    SVNProjectConfigurationPropertyType,
                    new SVNProjectConfigurationProperty(
                            configuration,
                            '/some/trunk'
                    )
            )
            propertyService.editProperty(
                    branch,
                    SVNBranchConfigurationPropertyType,
                    new SVNBranchConfigurationProperty(
                            '/some/branches/${sourceName}',
                            new ConfiguredBuildSvnRevisionLink<>(
                                    link,
                                    new RevisionPattern('${sourceName}*-{revision}')
                            ).toServiceConfiguration()
                    )
            )
        }
        // As a template
        asUser().with(branch, ProjectEdit).call {
            branchTemplateService.setTemplateDefinition(
                    branch.id,
                    new TemplateDefinition(
                            [
                                    new TemplateParameter('scmPath', '', '')
                            ],
                            new ServiceConfiguration(
                                    '',
                                    JsonUtils.object().end()
                            ),
                            TemplateSynchronisationAbsencePolicy.DELETE,
                            0
                    )
            )
        }

        // Instantiates this template
        def instance = asUser().with(branch, ProjectEdit).call {
            branchTemplateService.createTemplateInstance(
                    branch.id,
                    new BranchTemplateInstanceSingleRequest(
                            "1.0",
                            true,
                            [scmPath: 'branches/1.0']
                    )
            )
        }

        // Gets the SVN branch property
        def property = asUser().with(instance, ProjectEdit).call {
            propertyService.getProperty(
                    instance,
                    SVNBranchConfigurationPropertyType
            )
        }

        // Checks the instance's property
        assert !property.empty
        assert property.value.buildRevisionLink.id == 'revisionPattern'
        assert property.value.buildRevisionLink.data.pattern.asText() as String == '1.0*-{revision}'
    }

}
