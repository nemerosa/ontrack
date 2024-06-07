package net.nemerosa.ontrack.extension.stale

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.*
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

    override fun getName(): String = "Auto-disabling of branches based on patterns"

    override fun getDescription(): String =
        "Given a list of patterns and their behaviour, allows the disabling of branches based on their Ontrack names."

    override fun getSupportedEntityTypes() = setOf(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
        securityService.isProjectFunctionGranted(entity, ProjectEdit::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun fromClient(node: JsonNode): AutoDisablingBranchPatternsProperty =
        fromStorage(node)

    override fun fromStorage(node: JsonNode): AutoDisablingBranchPatternsProperty =
        node.parse<AutoDisablingBranchPatternsProperty>()

    override fun replaceValue(
        value: AutoDisablingBranchPatternsProperty,
        replacementFunction: Function<String, String?>,
    ): AutoDisablingBranchPatternsProperty = value

    override fun getEditionForm(entity: ProjectEntity, value: AutoDisablingBranchPatternsProperty?): Form =
        Form.create()
            .multiform(
                property = AutoDisablingBranchPatternsProperty::items,
                items = value?.items
            ) {
                Form.create()
                    .multiStrings(AutoDisablingBranchPatternsPropertyItem::includes, null)
                    .multiStrings(AutoDisablingBranchPatternsPropertyItem::excludes, null)
                    .enumField(AutoDisablingBranchPatternsPropertyItem::mode, null)
                    .intField(AutoDisablingBranchPatternsPropertyItem::keepLast, null, min = 1)
            }
}