package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.PromotionRunRequest
import net.nemerosa.ontrack.model.structure.PropertyCreationRequest
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class PromotionRunControllerIT extends AbstractWebTestSupport {

    @Autowired
    private PromotionRunController controller

    @Autowired
    private PropertyService propertyService

    @Autowired
    private JenkinsConfigurationService jenkinsConfigurationService

    @Test
    void 'New promotion run'() {
        // Promotion level
        def promotionLevel = doCreatePromotionLevel()
        // Build
        def build = doCreateBuild(
                promotionLevel.branch,
                nd('1', "Build 1")
        )
        // Promotion run request
        PromotionRunRequest request = new PromotionRunRequest(
                promotionLevel.id(),
                '',
                Time.now(),
                "Run",
                []
        )
        // Call
        def run = asUser().with(promotionLevel, ProjectEdit).call {
            controller.newPromotionRun(build.id, request)
        }
        // Checks
        assert run != null
    }

    @Test
    void 'New promotion run with properties'() {
        // Promotion level
        def promotionLevel = doCreatePromotionLevel()
        // Build
        def build = doCreateBuild(
                promotionLevel.branch,
                nd('1', "Build 1")
        )
        // Promotion run request
        PromotionRunRequest request = new PromotionRunRequest(
                promotionLevel.id(),
                '',
                Time.now(),
                "Run",
                [
                        new PropertyCreationRequest(
                                JenkinsBuildPropertyType.class.name,
                                JsonUtils.object()
                                        .with('configuration', 'MyConfig')
                                        .with('job', 'MyJob')
                                        .with('build', 1)
                                        .end()
                        )
                ]
        )
        // Creates a Jenkins configuration
        asUser().with(GlobalSettings).call {
            jenkinsConfigurationService.newConfiguration(
                    new JenkinsConfiguration(
                            'MyConfig',
                            'http://jenkins.nemerosa.net',
                            'test',
                            'test'
                    )
            )
        }
        // Call
        def run = asUser().with(promotionLevel, ProjectEdit).call {
            controller.newPromotionRun(build.id, request)
        }
        // Checks
        assert run != null
        def property = propertyService.getProperty(run, JenkinsBuildPropertyType)
        assert !property.empty
        assert property.value.build == 1
        assert property.value.job == 'MyJob'
    }

}