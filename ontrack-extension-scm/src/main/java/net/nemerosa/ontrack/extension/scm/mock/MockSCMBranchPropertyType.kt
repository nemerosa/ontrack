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
@Profile(value = [RunProfile.DEV, RunProfile.ACC])
class MockSCMBranchPropertyType(
    extensionFeature: SCMExtensionFeature,
) : AbstractPropertyType<MockSCMBranchProperty>(extensionFeature) {

    override fun getName(): String = "Mock SCM"

    override fun getDescription(): String = "Mock SCM used for testing only"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = setOf(ProjectEntityType.BRANCH)

    override fun canEdit(entity: ProjectEntity?, securityService: SecurityService?): Boolean = true

    override fun canView(entity: ProjectEntity?, securityService: SecurityService?): Boolean = true

    override fun fromClient(node: JsonNode): MockSCMBranchProperty = node.parse()

    override fun fromStorage(node: JsonNode): MockSCMBranchProperty = node.parse()

    override fun replaceValue(
        value: MockSCMBranchProperty,
        replacementFunction: Function<String, String>,
    ): MockSCMBranchProperty = value

    override fun getEditionForm(entity: ProjectEntity, value: MockSCMBranchProperty?): Form = Form.create()
}
