package net.nemerosa.ontrack.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.json.getIntField
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.model.exceptions.PropertyTypeNotFoundException
import net.nemerosa.ontrack.model.form.Date
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.CoreBuildFilterInvalidException
import net.nemerosa.ontrack.repository.CoreBuildFilterRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class StandardBuildFilterProvider(
    private val structureService: StructureService,
    private val validationRunStatusService: ValidationRunStatusService,
    private val propertyService: PropertyService,
    private val coreBuildFilterRepository: CoreBuildFilterRepository,
) : AbstractBuildFilterProvider<StandardBuildFilterData>() {

    override val type: String = StandardBuildFilterProvider::class.java.name

    override val name: String = "Standard filter"

    override val isPredefined: Boolean = false

    override fun filterBranchBuilds(branch: Branch, data: StandardBuildFilterData?): List<Build> =
            try {
                coreBuildFilterRepository.standardFilter(
                        branch,
                        data ?: StandardBuildFilterData.of(10)
                ) { type -> propertyService.getPropertyTypeByName<Any>(type) }
            } catch (_: CoreBuildFilterInvalidException) {
                emptyList()
            }

    override fun filterBranchBuildsWithPagination(
        branch: Branch,
        data: StandardBuildFilterData?,
        offset: Int,
        size: Int,
    ): PaginatedList<Build> = try {
        coreBuildFilterRepository.standardFilterPagination(
                branch,
                data ?: StandardBuildFilterData.of(size),
                offset,
                size
        ) { type -> propertyService.getPropertyTypeByName<Any>(type) }
    } catch (_: CoreBuildFilterInvalidException) {
        PaginatedList.empty()
    }

    override fun blankForm(branchId: ID): Form {
        // Promotion levels for this branch
        val promotionLevels = structureService.getPromotionLevelListForBranch(branchId)
        // Validation stamps for this branch
        val validationStamps = structureService.getValidationStampListForBranch(branchId)
        // List of validation run statuses
        val statuses = ArrayList(validationRunStatusService.validationRunStatusList)
        // List of properties for a build

        val properties = propertyService.propertyTypes
            .filter { type -> type.supportedEntityTypes.contains(ProjectEntityType.BUILD) }
            .map { type -> PropertyTypeDescriptor.of(type) }
        // Form
        return Form.create()
            .with(
                Selection.of("sincePromotionLevel")
                    .label("Since promotion level")
                    .help("Builds since the last one which was promoted to this level")
                    .items(promotionLevels)
                    .itemId("name")
                    .optional()
            )
            .with(
                Selection.of("withPromotionLevel")
                    .label("With promotion level")
                    .help("Builds with this promotion level")
                    .items(promotionLevels)
                    .itemId("name")
                    .optional()
            )
            .with(
                Date.of("afterDate")
                    .label("Build after")
                    .help("Build created after or on this date")
                    .optional()
            )
            .with(
                Date.of("beforeDate")
                    .label("Build before")
                    .help("Build created before or on this date")
                    .optional()
            )
            .with(
                Selection.of("sinceValidationStamp")
                    .label("Since validation stamp")
                    .help("Builds since the last one which had this validation stamp")
                    .items(validationStamps)
                    .itemId("name")
                    .optional()
            )
            .with(
                Selection.of("sinceValidationStampStatus")
                    .label("... with status")
                    .items(statuses)
                    .optional()
            )
            .with(
                Selection.of("withValidationStamp")
                    .label("With validation stamp")
                    .help("Builds with this validation stamp")
                    .items(validationStamps)
                    .itemId("name")
                    .optional()
            )
            .with(
                Selection.of("withValidationStampStatus")
                    .label("... with status")
                    .items(statuses)
                    .optional()
            )
            .with(
                Selection.of("sinceProperty")
                    .label("Since property")
                    .items(properties)
                    .itemId("typeName")
                    .optional()
            )
            .with(
                Text.of("sincePropertyValue")
                    .label("... with value")
                    .length(40)
                    .optional()
            )
            .with(
                Selection.of("withProperty")
                    .label("With property")
                    .items(properties)
                    .itemId("typeName")
                    .optional()
            )
            .with(
                Text.of("withPropertyValue")
                    .label("... with value")
                    .length(40)
                    .optional()
            )
            .with(
                Text.of("linkedFrom")
                    .label("Linked from")
                    .help(
                        "The build must be linked FROM the builds selected by the pattern.\n" + "Syntax: PRJ:BLD where PRJ is a project name and BLD a build expression - with * as placeholder"
                    )
                    .length(40)
                    .optional()
            )
            .with(
                Text.of("linkedFromPromotion")
                    .label("Linked from promotion level")
                    .help("The build must be linked FROM a build having this promotion (requires \"Linked from\")")
                    .length(40)
                    .optional()
            )
            .with(
                Text.of("linkedTo")
                    .label("Linked to")
                    .help(
                        "The build must be linked TO the builds selected by the pattern.\n" + "Syntax: PRJ:BLD where PRJ is a project name and BLD a build expression - with * as placeholder"
                    )
                    .length(40)
                    .optional()
            )
            .with(
                Text.of("linkedToPromotion")
                    .label("Linked to promotion level")
                    .help("The build must be linked TO a build having this promotion (requires \"Linked to\")")
                    .length(40)
                    .optional()
            )
    }

    override fun fill(form: Form, data: StandardBuildFilterData): Form {
        return form
            .fill("sincePromotionLevel", data.sincePromotionLevel)
            .fill("withPromotionLevel", data.withPromotionLevel)
            .fill("afterDate", data.afterDate)
            .fill("beforeDate", data.beforeDate)
            .fill("sinceValidationStamp", data.sinceValidationStamp)
            .fill("sinceValidationStampStatus", data.sinceValidationStampStatus)
            .fill("withValidationStamp", data.withValidationStamp)
            .fill("withValidationStampStatus", data.withValidationStampStatus)
            .fill("sinceProperty", data.sinceProperty)
            .fill("sincePropertyValue", data.sincePropertyValue)
            .fill("withProperty", data.withProperty)
            .fill("withPropertyValue", data.withPropertyValue)
            .fill("linkedFrom", data.linkedFrom)
            .fill("linkedFromPromotion", data.linkedFromPromotion)
            .fill("linkedToPromotion", data.linkedToPromotion)
    }

    override fun parse(data: JsonNode): StandardBuildFilterData? =
        StandardBuildFilterData.of(data.getIntField("count") ?: 10)
            .withSincePromotionLevel(data.getTextField("sincePromotionLevel"))
            .withWithPromotionLevel(JsonUtils.get(data, "withPromotionLevel", null))
            .withAfterDate(JsonUtils.getDate(data, "afterDate", null))
            .withBeforeDate(JsonUtils.getDate(data, "beforeDate", null))
            .withSinceValidationStamp(JsonUtils.get(data, "sinceValidationStamp", null))
            .withSinceValidationStampStatus(JsonUtils.get(data, "sinceValidationStampStatus", null))
            .withWithValidationStamp(JsonUtils.get(data, "withValidationStamp", null))
            .withWithValidationStampStatus(JsonUtils.get(data, "withValidationStampStatus", null))
            .withSinceProperty(JsonUtils.get(data, "sinceProperty", null))
            .withSincePropertyValue(JsonUtils.get(data, "sincePropertyValue", null))
            .withWithProperty(JsonUtils.get(data, "withProperty", null))
            .withWithPropertyValue(JsonUtils.get(data, "withPropertyValue", null))
            .withLinkedFrom(JsonUtils.get(data, "linkedFrom", null))
            .withLinkedFromPromotion(JsonUtils.get(data, "linkedFromPromotion", null))
            .withLinkedTo(JsonUtils.get(data, "linkedTo", null))
            .withLinkedToPromotion(JsonUtils.get(data, "linkedToPromotion", null))

    override fun validateData(branch: Branch, data: StandardBuildFilterData?): String? =
        if (data != null) {
            // Since promotion
            validatePromotion(branch, data.sincePromotionLevel, "Since promotion")
            // With promotion
                ?: validatePromotion(branch, data.withPromotionLevel, "With promotion")
                // Since validation
                ?: validateValidation(branch, data.sinceValidationStamp, "Since validation")
                // With validation
                ?: validateValidation(branch, data.withValidationStamp, "With validation")
                // Since property
                ?: validateProperty(data.sinceProperty, "Since property")
                // With property
                ?: validateProperty(data.withProperty, "With property")
        } else {
            null
        }

    private fun validateProperty(property: String?, field: String): String? {
        return property
            ?.let {
                try {
                    propertyService.getPropertyTypeByName<Any>(property)
                    null
                } catch (ex: PropertyTypeNotFoundException) {
                    """Property "$property" does not exist for filter "$field"."""
                }
            }
    }

    private fun validateValidation(branch: Branch, validationStamp: String?, field: String): String? {
        return validationStamp
            ?.let {
                if (structureService.findValidationStampByName(
                        branch.project.name,
                        branch.name,
                        it
                    ).isPresent
                ) {
                    null
                } else {
                    """Validation stamp $validationStamp does not exist for filter "$field"."""
                }
            }
    }

    private fun validatePromotion(branch: Branch, promotionLevel: String?, field: String): String? {
        return promotionLevel
            ?.let {
                if (structureService.findPromotionLevelByName(
                        branch.project.name,
                        branch.name,
                        it
                    ).isPresent
                ) {
                    null
                } else {
                    """Promotion level $promotionLevel does not exist for filter "$field"."""
                }
            }
    }

}
