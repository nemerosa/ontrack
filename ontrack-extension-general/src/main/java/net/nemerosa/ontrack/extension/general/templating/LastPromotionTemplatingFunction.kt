package net.nemerosa.ontrack.extension.general.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import net.nemerosa.ontrack.model.templating.TemplatingGeneralException
import net.nemerosa.ontrack.model.templating.getBooleanTemplatingParam
import net.nemerosa.ontrack.model.templating.getRequiredTemplatingParam
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
@APIDescription("Renders the last build having a given promotion in a project")
@Documentation(LastPromotionTemplatingFunctionParameters::class)
@DocumentationExampleCode(
    """
       #.lastPromotion?project=prj&promotion=BRONZE 
    """
)
class LastPromotionTemplatingFunction(
    private val structureService: StructureService,
    private val buildFilterService: BuildFilterService,
    private val buildDisplayNameService: BuildDisplayNameService,
) : TemplatingFunction {

    override val id: String = "lastPromotion"

    override fun render(
        configMap: Map<String, String>,
        context: Map<String, Any>,
        renderer: EventRenderer,
        expressionResolver: (expression: String) -> String
    ): String {

        // Getting the configuration
        val projectName = configMap.getRequiredTemplatingParam(LastPromotionTemplatingFunctionParameters::project.name)
        val branchName = configMap[LastPromotionTemplatingFunctionParameters::branch.name]
        val promotion = configMap.getRequiredTemplatingParam(LastPromotionTemplatingFunctionParameters::promotion.name)
        val name = configMap[LastPromotionTemplatingFunctionParameters::name.name] ?: "auto"
        val link = configMap.getBooleanTemplatingParam(LastPromotionTemplatingFunctionParameters::link.name, true)

        // Looking for the project
        val project = structureService.findProjectByName(projectName).getOrNull()
            ?: throw ProjectNotFoundException(projectName)

        // Looking for the promotion on the project only
        val build = if (branchName.isNullOrBlank()) {
            structureService.buildSearch(
                projectId = project.id,
                form = BuildSearchForm(maximumCount = 1, promotionName = promotion)
            ).firstOrNull()
        }
        // Looking for the promotion in a branch
        else {
            // Looking for the branch
            val branch = structureService.findBranchByName(
                project = projectName,
                branch = branchName,
            ).getOrNull()
                ?: throw BranchNotFoundException(projectName, branchName)
            // Filtering
            buildFilterService.standardFilterProviderData(1)
                .withWithPromotionLevel(promotion)
                .build()
                .filterBranchBuilds(branch)
                .firstOrNull()
        }

        // If no build found, error
        if (build == null) {
            throw TemplatingGeneralException("Cannot find build with last promotion")
        }

        // Getting the name of the build
        val displayName = buildDisplayNameService.getFirstBuildDisplayName(build)
        val buildName = when (name) {
            "auto" -> displayName ?: build.name
            "name" -> build.name
            "release" -> displayName ?: throw TemplatingGeneralException("Build has not release name")
            else -> throw TemplatingGeneralException("Unknown name mode: $name")
        }

        // Rendering as link or not
        return if (link) {
            renderer.render(build, buildName)
        } else {
            buildName
        }

    }

}