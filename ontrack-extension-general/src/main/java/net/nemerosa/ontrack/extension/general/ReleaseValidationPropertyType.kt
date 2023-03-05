package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.textField
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertySearchArguments
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class ReleaseValidationPropertyType(
    extensionFeature: GeneralExtensionFeature,
) : AbstractPropertyType<ReleaseValidationProperty>(extensionFeature) {

    override fun getName(): String = "Validation on release/label"

    override fun getDescription(): String = "When set, adding a release/label on a build will also validate this build."

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = setOf(ProjectEntityType.BRANCH)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
        securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun fromClient(node: JsonNode): ReleaseValidationProperty = fromStorage(node)

    override fun fromStorage(node: JsonNode): ReleaseValidationProperty = node.parse()

    override fun replaceValue(
        value: ReleaseValidationProperty,
        replacementFunction: Function<String, String>,
    ) = ReleaseValidationProperty(
        validation = replacementFunction.apply(value.validation)
    )

    override fun getEditionForm(entity: ProjectEntity, value: ReleaseValidationProperty?): Form = Form.create()
        .textField(ReleaseValidationProperty::validation, value?.validation)

    override fun containsValue(value: ReleaseValidationProperty, propertyValue: String): Boolean =
        value.validation == propertyValue

    override fun getSearchArguments(token: String): PropertySearchArguments? =
        if (token.isNotBlank()) {
            PropertySearchArguments(
                null,
                "UPPER(pp.json->>'validation') = UPPER(:token)",
                mapOf("token" to token)
            )
        } else {
            null
        }
}