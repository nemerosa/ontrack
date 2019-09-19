package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.MultiSelection
import net.nemerosa.ontrack.model.form.MultiStrings
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.SelectableItem
import org.springframework.stereotype.Component
import java.util.function.Function
import java.util.stream.Collectors

/**
 * Definition of the "Promotion dependencies" property type.
 */
@Component
class PromotionDependenciesPropertyType(
        extensionFeature: GeneralExtensionFeature,
        private val structureService: StructureService
) : AbstractPropertyType<PromotionDependenciesProperty>(extensionFeature) {

    override fun getName(): String = "Promotion dependencies"

    override fun getDescription(): String =
            "List of promotions a promotion depends on before being applied."

    /**
     * Only for promotions
     */
    override fun getSupportedEntityTypes(): Set<ProjectEntityType> =
            setOf(ProjectEntityType.PROMOTION_LEVEL)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
            securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean =
            true

    override fun getEditionForm(entity: ProjectEntity, value: PromotionDependenciesProperty?): Form {
        val promotion = entity as PromotionLevel
        val promotions = structureService.getPromotionLevelListForBranch(
                promotion.branch.id
        )
        return Form.create()
                .with(
                        MultiSelection.of("dependencies")
                                .label("Dependencies")
                                .help("List of promotions this promotion depends on.")
                                .items(
                                        promotions.filter {
                                            it.id != promotion.id
                                        }.map {
                                            SelectableItem(
                                                    it.name in value?.dependencies ?: emptyList(),
                                                    it.name,
                                                    it.name
                                            )
                                        }
                                )
                )
    }

    override fun fromClient(node: JsonNode): PromotionDependenciesProperty = fromStorage(node)

    override fun fromStorage(node: JsonNode): PromotionDependenciesProperty = node.parse()

    override fun replaceValue(value: PromotionDependenciesProperty, replacementFunction: Function<String, String>): PromotionDependenciesProperty =
            value
}