package net.nemerosa.ontrack.extension.scm.mock

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
@Profile(RunProfile.ACC, RunProfile.UNIT_TEST)
class MockSCMProjectPropertyType(
    extensionFeature: SCMExtensionFeature,
): AbstractPropertyType<MockSCMProjectProperty>(extensionFeature) {

    override fun getName(): String = "Mock SCM"

    override fun getDescription(): String = "Mock SCM used for testing only"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = setOf(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity?, securityService: SecurityService?): Boolean = true

    override fun canView(entity: ProjectEntity?, securityService: SecurityService?): Boolean = true

    override fun fromClient(node: JsonNode): MockSCMProjectProperty = node.parse()

    override fun fromStorage(node: JsonNode): MockSCMProjectProperty = node.parse()

    override fun replaceValue(
        value: MockSCMProjectProperty,
        replacementFunction: Function<String, String>,
    ): MockSCMProjectProperty = value

    override fun getEditionForm(entity: ProjectEntity, value: MockSCMProjectProperty?): Form = Form.create()
}