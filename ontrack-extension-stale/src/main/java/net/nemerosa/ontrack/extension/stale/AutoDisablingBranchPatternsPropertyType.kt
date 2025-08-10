package net.nemerosa.ontrack.extension.stale

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class AutoDisablingBranchPatternsPropertyType(
    staleExtensionFeature: StaleExtensionFeature,
) : AbstractPropertyType<AutoDisablingBranchPatternsProperty>(
    staleExtensionFeature
) {

    override val name: String = "Auto-disabling of branches based on patterns"

    override val description: String =
        "Given a list of patterns and their behaviour, allows the disabling of branches based on their Ontrack names."

    override val supportedEntityTypes = setOf(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
        securityService.isProjectFunctionGranted(entity, ProjectEdit::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun fromClient(node: JsonNode): AutoDisablingBranchPatternsProperty =
        fromStorage(node)

    override fun fromStorage(node: JsonNode): AutoDisablingBranchPatternsProperty =
        node.parse<AutoDisablingBranchPatternsProperty>()

    @Deprecated("Will be removed in V5")
    override fun replaceValue(
        value: AutoDisablingBranchPatternsProperty,
        replacementFunction: Function<String, String>,
    ): AutoDisablingBranchPatternsProperty = value
}