package net.nemerosa.ontrack.extension.api.support

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertySearchArguments
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class TestSimplePropertyType(
    extensionFeature: TestExtensionFeature
) : AbstractPropertyType<TestSimpleProperty>(extensionFeature) {

    override fun getName(): String = "Simple value"

    override fun getDescription(): String = "Value."

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.allOf(ProjectEntityType::class.java)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
        securityService.isProjectFunctionGranted(entity, ProjectEdit::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun getEditionForm(entity: ProjectEntity, value: TestSimpleProperty?): Form = create()
        .with(
            Text.of("value")
                .label("Value")
                .value(value?.value ?: "")
        )

    override fun fromClient(node: JsonNode): TestSimpleProperty = fromStorage(node)

    override fun fromStorage(node: JsonNode): TestSimpleProperty = parse(node, TestSimpleProperty::class.java)

    override fun replaceValue(
        value: TestSimpleProperty,
        replacementFunction: Function<String, String>
    ): TestSimpleProperty {
        return TestSimpleProperty(
            replacementFunction.apply(value.value)
        )
    }

    override fun getSearchArguments(token: String): PropertySearchArguments? {
        return PropertySearchArguments(
            null,
            "pp.json->>'value' like :value",
            Collections.singletonMap("value", "%$token%")
        )
    }

    override fun containsValue(value: TestSimpleProperty, propertyValue: String): Boolean =
        value.value.contains(propertyValue, ignoreCase = true)
}
