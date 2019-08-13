package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class PreviousPromotionConditionPropertyType(
        extensionFeature: GeneralExtensionFeature
) : AbstractPropertyType<PreviousPromotionConditionProperty>(extensionFeature) {

    override fun getName(): String = "Previous promotion condition"

    override fun getDescription(): String =
            "Makes a promotion conditional based on the fact that a previous promotion has been granted."

    /**
     * Project, branch and promotion levels are supported.
     */
    override fun getSupportedEntityTypes(): Set<ProjectEntityType> =
            setOf(
                    ProjectEntityType.PROJECT,
                    ProjectEntityType.BRANCH,
                    ProjectEntityType.PROMOTION_LEVEL
            )

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
            securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean =
            true

    override fun getEditionForm(entity: ProjectEntity, value: PreviousPromotionConditionProperty?): Form {
        return Form.create()
                .with(
                        YesNo.of("previousPromotionRequired")
                                .label("Previous promotion required")
                                .help("Makes a promotion conditional based on the fact that a previous promotion has been granted.")
                                .value(value?.previousPromotionRequired ?: false)
                )
    }

    override fun fromClient(node: JsonNode): PreviousPromotionConditionProperty = fromStorage(node)

    override fun fromStorage(node: JsonNode): PreviousPromotionConditionProperty = node.parse()

    override fun replaceValue(value: PreviousPromotionConditionProperty, replacementFunction: Function<String, String>): PreviousPromotionConditionProperty =
            value
}