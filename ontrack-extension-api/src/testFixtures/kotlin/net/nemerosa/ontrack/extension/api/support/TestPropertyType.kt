package net.nemerosa.ontrack.extension.api.support

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class TestPropertyType(
    extensionFeature: TestExtensionFeature
) : AbstractPropertyType<TestProperty>(extensionFeature),
    ConfigurationPropertyType<TestConfiguration, TestProperty> {

    override fun getName(): String {
        return "Configuration value"
    }

    override fun getDescription(): String {
        return "Value."
    }

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> {
        return EnumSet.allOf(ProjectEntityType::class.java)
    }

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity, ProjectEdit::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return true
    }

    override fun getEditionForm(entity: ProjectEntity, value: TestProperty): Form {
        return create()
            .with(
                Text.of("value")
                    .label("Value")
                    .value(value.value)
            )
    }

    override fun forStorage(value: TestProperty): JsonNode =
        mapOf(
            TestProperty::configuration.name to value.configuration.name,
            TestProperty::value.name to value.value,
        ).asJson()

    override fun fromClient(node: JsonNode): TestProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): TestProperty {
        return TestProperty(
            TestConfiguration.config(node.getRequiredTextField(TestProperty::configuration.name)),
            node.getRequiredTextField(TestProperty::value.name),
        )
    }

    override fun replaceValue(value: TestProperty, replacementFunction: Function<String, String>): TestProperty {
        return TestProperty(
            value.configuration,
            replacementFunction.apply(value.value)
        )
    }
}