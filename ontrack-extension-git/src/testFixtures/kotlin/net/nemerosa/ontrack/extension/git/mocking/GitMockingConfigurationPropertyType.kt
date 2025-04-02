package net.nemerosa.ontrack.extension.git.mocking

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class GitMockingConfigurationPropertyType(
        testExtensionFeature: TestExtensionFeature
) : AbstractPropertyType<GitMockingConfigurationProperty>(testExtensionFeature) {

    override fun getName(): String = "Mock Git configuration"

    override fun getDescription(): String = "Git configuration used for testing"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = setOf(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
            securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun getEditionForm(entity: ProjectEntity, value: GitMockingConfigurationProperty): Form = error("Will not be used")

    override fun fromClient(node: JsonNode): GitMockingConfigurationProperty = error("Will not be used")

    override fun fromStorage(node: JsonNode): GitMockingConfigurationProperty = node.parse()

    override fun replaceValue(value: GitMockingConfigurationProperty, replacementFunction: Function<String, String>): GitMockingConfigurationProperty = error("Will not be used")
}