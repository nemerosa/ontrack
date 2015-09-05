package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.boot.properties.AutoPromotionLevelProperty
import net.nemerosa.ontrack.boot.properties.AutoPromotionLevelPropertyType
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.*
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

    @Autowired
    private PredefinedPromotionLevelService predefinedPromotionLevelService

    @Test
    void 'Auto creation of promotion levels must preserve the order'() {
        asUser().with(GlobalSettings).call {
            // Creating three predefined promotion levels
            def copper = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(nd('COPPER', ''))
            )
            def bronze = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(nd('BRONZE', ''))
            )
            def silver = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(nd('SILVER', ''))
            )
            def gold = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(nd('GOLD', ''))
            )
            // Checking their order
            assert predefinedPromotionLevelService.getPredefinedPromotionLevels().collect { it.name } ==
                    ['COPPER', 'BRONZE', 'SILVER', 'GOLD']
            // Reordering
            predefinedPromotionLevelService.reorderPromotionLevels(
                    new Reordering([
                            gold.id.get(),
                            silver.id.get(),
                            bronze.id.get(),
                            copper.id.get(),
                    ])
            )
            // Checking their order
            assert predefinedPromotionLevelService.getPredefinedPromotionLevels().collect { it.name } ==
                    ['GOLD', 'SILVER', 'BRONZE', 'COPPER']
        }
        // Creating a build
        Build build = doCreateBuild()
        Branch branch = build.branch
        Project project = build.project
        // Configuring the project for auto creation of promotion levels
        asUser().with(project, ProjectEdit).call {
            propertyService.editProperty(
                    project,
                    AutoPromotionLevelPropertyType,
                    new AutoPromotionLevelProperty(true)
            )
        }
        // Promoting the build
        asUser().with(project, ProjectEdit).call {
            controller.newPromotionRun(build.id, new PromotionRunRequest(
                    null, 'BRONZE', Time.now(), '', []
            ))
            controller.newPromotionRun(build.id, new PromotionRunRequest(
                    null, 'GOLD', Time.now(), '', []
            ))
            controller.newPromotionRun(build.id, new PromotionRunRequest(
                    null, 'COPPER', Time.now(), '', []
            ))
            controller.newPromotionRun(build.id, new PromotionRunRequest(
                    null, 'SILVER', Time.now(), '', []
            ))
        }
        // Controlling the promotion levels which have been created for the branch
        asUser().withView(project).call {
            assert structureService.getPromotionLevelListForBranch(branch.id).collect { it.name } ==
                    ['GOLD', 'SILVER', 'BRONZE', 'COPPER']
        }
    }

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