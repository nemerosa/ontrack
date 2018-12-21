package net.nemerosa.ontrack.extension.git.property

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.EnumSet
import java.util.function.Function

@Component
class GitCommitPropertyType @Autowired
constructor(extensionFeature: GitExtensionFeature) : AbstractPropertyType<GitCommitProperty>(extensionFeature) {

    override fun getName(): String {
        return "Git commit"
    }

    override fun getDescription(): String {
        return "Git commit"
    }

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> {
        return EnumSet.of(ProjectEntityType.BUILD)
    }

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity, BuildCreate::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return true
    }

    override fun getEditionForm(entity: ProjectEntity, value: GitCommitProperty?): Form {
        return Form.create()
                .with(
                        Text.of("commit")
                                .label("Git commit")
                                .value(value?.commit ?: "HEAD")
                )
    }

    override fun fromClient(node: JsonNode): GitCommitProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): GitCommitProperty {
        return GitCommitProperty(
                JsonUtils.get(node, "commit")
        )
    }

    override fun getSearchKey(value: GitCommitProperty): String {
        return value.commit
    }

    override fun replaceValue(value: GitCommitProperty, replacementFunction: Function<String, String>): GitCommitProperty {
        // A commit is immutable...
        return value
    }

}
