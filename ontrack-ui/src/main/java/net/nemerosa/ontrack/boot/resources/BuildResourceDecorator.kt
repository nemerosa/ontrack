package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.*
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.ui.resource.*
import net.nemerosa.ontrack.ui.resource.LinkDefinitions.link
import net.nemerosa.ontrack.ui.resource.LinkDefinitions.page
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class BuildResourceDecorator(
        private val resourceDecorationContributorService: ResourceDecorationContributorService
) : AbstractLinkResourceDecorator<Build>(Build::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<Build>> {
        return listOf(
                link(
                        Link.SELF
                ) { build -> on(BuildController::class.java).getBuild(build.id) },
                // Other linked resources
                link(
                        "_lastPromotionRuns"
                ) { build -> on(PromotionRunController::class.java).getLastPromotionRuns(build.id) },
                link(
                        "_promotionRuns"
                ) { build -> on(PromotionRunController::class.java).getPromotionRuns(build.id) },
                link(
                        "_validationRuns"
                ) { build -> on(ValidationRunController::class.java).getValidationRuns(build.id) },
                link(
                        "_validationStampRunViews"
                ) { build -> on(ValidationRunController::class.java).getValidationStampRunViews(build.id) },
                // Creation of a promoted run
                "_promote" linkTo { build: Build ->
                    on(PromotionRunController::class.java).newPromotionRunForm(build.id)
                } linkIf PromotionRunCreate::class,
                // Creation of a validation run
                "_validate" linkTo { build: Build ->
                    on(ValidationRunController::class.java).newValidationRunForm(build.id)
                } linkIf ValidationRunCreate::class,
                // Actual properties for this build
                link(
                        "_properties"
                ) { build -> on(PropertyController::class.java).getProperties(ProjectEntityType.BUILD, build.id) },
                // Actions
                link(
                        "_actions"
                ) { build -> on(ProjectEntityExtensionController::class.java).getActions(ProjectEntityType.BUILD, build.id) },
                // Extra information
                link(
                        "_extra"
                ) { build -> on(ProjectEntityExtensionController::class.java).getInformation(ProjectEntityType.BUILD, build.id) },
                // Update link
                Link.UPDATE linkTo { build: Build ->
                    on(BuildController::class.java).updateBuild(build.id, null)
                } linkIf BuildEdit::class,
                // Delete link
                Link.DELETE linkTo { build: Build ->
                    on(BuildController::class.java).deleteBuild(build.id)
                } linkIf BuildDelete::class,
                // Decorations
                link(
                        "_decorations"
                ) { build -> on(DecorationsController::class.java).getDecorations(build.projectEntityType, build.id) },
                // Events
                link(
                        "_events"
                ) { build -> on(EventController::class.java).getEvents(build.projectEntityType, build.id, 0, 10) },
                // Signature change
                "_signature" linkTo { build: Build ->
                    on(BuildController::class.java).updateBuildSignatureForm(build.id)
                } linkIf ProjectEdit::class,
                // Previous & next build
                link(
                        "_previous"
                ) { build -> on(BuildController::class.java).getPreviousBuild(build.id) },
                link(
                        "_next"
                ) { build -> on(BuildController::class.java).getNextBuild(build.id) },
                // Build links
                "_buildLinksFrom" linkTo { build -> on(BuildController::class.java).getBuildLinksFrom(build.id, 0, 10) },
                // Run info
                link(
                        "_runInfo"
                ) { build -> on(RunInfoController::class.java).getRunInfo(build.runnableEntityType, build.id()) },
                // Page
                page()
        ) + resourceDecorationContributorService.getLinkDefinitions(ProjectEntityType.BUILD)
    }

}
