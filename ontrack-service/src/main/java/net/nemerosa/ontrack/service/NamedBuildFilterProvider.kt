package net.nemerosa.ontrack.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.CoreBuildFilterRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
@Deprecated("Will be removed in V5")
class NamedBuildFilterProvider(
    private val structureService: StructureService,
    private val filterRepository: CoreBuildFilterRepository,
) : AbstractBuildFilterProvider<NamedBuildFilterData>() {
    override val type: String = NamedBuildFilterProvider::class.java.name
    override val name: String = "Name filter (deprecated)"
    override val isPredefined: Boolean = false

    override fun blankForm(branchId: ID): Form {
        // Promotion levels for this branch
        val promotionLevels: List<PromotionLevel?> = structureService.getPromotionLevelListForBranch(branchId)
        // Form
        return create()
            .with(
                Int.of("count")
                    .label("Maximum count")
                    .help("Maximum number of builds to display")
                    .min(1)
                    .value(10)
            )
            .with(
                Text.of("fromBuild")
                    .label("From build")
                    .help(
                        "Expression to identify a list of build. Only the most recent one is kept. " +
                                "* (star) can be used as a placeholder."
                    )
            )
            .with(
                Text.of("toBuild")
                    .label("To build")
                    .optional()
                    .help(
                        "Optional expression to identify a list of build. Only the most recent one is kept. " +
                                "If unset, the first build that does not comply with the \"from build\" expression is kept by default. " +
                                "* (star) can be used as a placeholder."
                    )
            )
            .with(
                Selection.of("withPromotionLevel")
                    .label("With promotion level")
                    .help("Optional. If set, restrict both \"from\" and \"to\" list to the builds with a promotion level.")
                    .items(promotionLevels)
                    .itemId("name")
                    .optional()
            )
    }

    override fun fill(form: Form, data: NamedBuildFilterData): Form {
        return form
            .fill("count", data.count)
            .fill("fromBuild", data.fromBuild)
            .fill("toBuild", data.toBuild)
            .fill("withPromotionLevel", data.withPromotionLevel)
    }

    override fun parse(data: JsonNode): NamedBuildFilterData? =
        NamedBuildFilterData(fromBuild = JsonUtils.get(data, "fromBuild", ""))
            .withCount(JsonUtils.getInt(data, "count", 10))
            .withToBuild(JsonUtils.get(data, "toBuild", null))
            .withWithPromotionLevel(JsonUtils.get(data, "withPromotionLevel", null))

    override fun filterBranchBuilds(branch: Branch, data: NamedBuildFilterData?): List<Build> {
        return filterRepository.nameFilter(
            branch,
            data!!.fromBuild,
            data.toBuild,
            data.withPromotionLevel,
            data.count
        )
    }
}