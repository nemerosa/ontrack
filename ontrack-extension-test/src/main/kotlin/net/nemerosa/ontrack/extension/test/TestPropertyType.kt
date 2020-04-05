package net.nemerosa.ontrack.extension.test

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class TestPropertyType @Autowired
constructor(extensionFeature: TestFeature) : AbstractPropertyType<TestProperty>(extensionFeature) {

    override fun getName(): String = "Test"

    override fun getDescription(): String = "Associates a text with the entity"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.allOf(ProjectEntityType::class.java)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
            securityService.isProjectFunctionGranted(entity, ProjectEdit::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun getEditionForm(entity: ProjectEntity, value: TestProperty?): Form =
            Form.create()
                    .with(
                            Text.of("value")
                                    .label("My value")
                                    .length(20)
                                    .value(value?.value)
                    )

    override fun fromClient(node: JsonNode): TestProperty = fromStorage(node)

    override fun fromStorage(node: JsonNode): TestProperty =
            AbstractPropertyType.parse(node, TestProperty::class.java)

    override fun replaceValue(value: TestProperty, replacementFunction: Function<String, String>): TestProperty =
            TestProperty(
                    replacementFunction.apply(value.value)
            )
}
