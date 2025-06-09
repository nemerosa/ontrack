package net.nemerosa.ontrack.extension.api.support

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class TestDecoratorPropertyType(
    extensionFeature: TestExtensionFeature
) : AbstractPropertyType<TestDecorationData?>(extensionFeature) {

    override fun getName(): String = "Decorator value"

    override fun getDescription(): String = "Value."

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.allOf(ProjectEntityType::class.java)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
        securityService.isProjectFunctionGranted(entity, ProjectEdit::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun fromClient(node: JsonNode): TestDecorationData {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): TestDecorationData {
        return parse(node, TestDecorationData::class.java)
    }

    override fun replaceValue(
        value: TestDecorationData,
        replacementFunction: Function<String, String>
    ): TestDecorationData {
        return TestDecorationData(
            replacementFunction.apply(value.value),
            value.isValid
        )
    }
}
