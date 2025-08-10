package net.nemerosa.ontrack.extension.scm.mock

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
@Profile(RunProfile.DEV)
class MockSCMBuildCommitPropertyType(
    extensionFeature: SCMExtensionFeature,
) : AbstractPropertyType<MockSCMBuildCommitProperty>(extensionFeature) {

    override val name: String = "Mock SCM commit"

    override val description: String = "Mock SCM used for testing only"

    override val supportedEntityTypes: Set<ProjectEntityType> = setOf(ProjectEntityType.BUILD)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun fromClient(node: JsonNode): MockSCMBuildCommitProperty = node.parse()

    override fun fromStorage(node: JsonNode): MockSCMBuildCommitProperty = node.parse()

    @Deprecated("Will be removed in V5")
    override fun replaceValue(
        value: MockSCMBuildCommitProperty,
        replacementFunction: Function<String, String>,
    ): MockSCMBuildCommitProperty = value

}
