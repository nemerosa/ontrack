package net.nemerosa.ontrack.extension.jira

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.MultiStrings
import net.nemerosa.ontrack.model.form.multiStrings
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

@Component
class JIRAFollowLinksPropertyType(
    extensionFeature: JIRAExtensionFeature
) : AbstractPropertyType<JIRAFollowLinksProperty>(extensionFeature) {

    override fun getName(): String = "JIRA Links to follow"

    override fun getDescription(): String = "List of links to follow when displaying information about an issue."

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> {
        return EnumSet.of(ProjectEntityType.PROJECT)
    }

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun getEditionForm(entity: ProjectEntity, value: JIRAFollowLinksProperty?): Form {
        return create()
            .multiStrings(JIRAFollowLinksProperty::linkNames, value?.linkNames)
    }

    override fun forStorage(value: JIRAFollowLinksProperty): JsonNode {
        return format(value)
    }

    override fun fromClient(node: JsonNode): JIRAFollowLinksProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): JIRAFollowLinksProperty {
        return parse(node, JIRAFollowLinksProperty::class.java)
    }

    override fun replaceValue(
        value: JIRAFollowLinksProperty,
        replacementFunction: Function<String, String>
    ): JIRAFollowLinksProperty {
        return JIRAFollowLinksProperty(
            value.linkNames.stream().map(replacementFunction).collect(Collectors.toList())
        )
    }
}
