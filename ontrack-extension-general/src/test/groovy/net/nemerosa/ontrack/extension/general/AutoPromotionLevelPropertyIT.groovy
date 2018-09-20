package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class AutoPromotionLevelPropertyIT extends AbstractServiceTestSupport {

    @Autowired
    private PropertyService propertyService

    @Autowired
    private PredefinedPromotionLevelService predefinedPromotionLevelService

    @Autowired
    private StructureService structureService

    @Test
    void 'Auto creation of promotion levels must preserve the order'() {
        asUser().with(GlobalSettings).call {
            // Clears all existing predefined promotion levels for isolation
            predefinedPromotionLevelService.predefinedPromotionLevels.each {
                predefinedPromotionLevelService.deletePredefinedPromotionLevel(it.id)
            }
            // Creating four predefined promotion levels
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
            structureService.getOrCreatePromotionLevel(branch, null, 'BRONZE')
            structureService.getOrCreatePromotionLevel(branch, null, 'GOLD')
            structureService.getOrCreatePromotionLevel(branch, null, 'COPPER')
            structureService.getOrCreatePromotionLevel(branch, null, 'SILVER')
        }
        // Controlling the promotion levels which have been created for the branch
        asUser().withView(project).call {
            assert structureService.getPromotionLevelListForBranch(branch.id).collect { it.name } ==
                    ['GOLD', 'SILVER', 'BRONZE', 'COPPER']
        }
    }
}
