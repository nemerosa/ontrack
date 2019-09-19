package net.nemerosa.ontrack.extension.git.branching

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.NamedEntries
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.support.NameValue
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class BranchingModelPropertyType(
        extensionFeature: GitExtensionFeature
) : AbstractPropertyType<BranchingModelProperty>(extensionFeature) {
    override fun getName(): String = "Branching Model"

    override fun getDescription(): String =
            "Defines the branching model used by a project"

    override fun getSupportedEntityTypes() =
            setOf(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
            securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean =
            true

    override fun getEditionForm(entity: ProjectEntity, value: BranchingModelProperty?): Form {
        val patterns: List<NameValue> = value?.patterns
                ?: BranchingModel.DEFAULT.patterns.map { (type, regex) -> NameValue(type, regex) }
        return Form.create()
                .with(
                        NamedEntries.of("patterns")
                                .label("List of branches")
                                .nameLabel("Type")
                                .valueLabel("Branches")
                                .addText("Add a branch type")
                                .help("Regular expressions to associate with branch types")
                                .value(patterns)
                )
    }

    override fun fromClient(node: JsonNode): BranchingModelProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): BranchingModelProperty {
        return JsonUtils.parse(node, BranchingModelProperty::class.java)
    }

    override fun getSearchKey(value: BranchingModelProperty): String = ""

    override fun replaceValue(value: BranchingModelProperty, replacementFunction: Function<String, String>) = value
}