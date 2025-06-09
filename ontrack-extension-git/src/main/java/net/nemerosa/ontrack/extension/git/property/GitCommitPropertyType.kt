package net.nemerosa.ontrack.extension.git.property

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertySearchArguments
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class GitCommitPropertyType(
    extensionFeature: GitExtensionFeature,
    private val gitService: GitService
) : AbstractPropertyType<GitCommitProperty>(extensionFeature) {

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

    override fun fromClient(node: JsonNode): GitCommitProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): GitCommitProperty {
        return GitCommitProperty(
            JsonUtils.get(node, "commit")
        )
    }

    override fun replaceValue(
        value: GitCommitProperty,
        replacementFunction: Function<String, String>
    ): GitCommitProperty {
        // A commit is immutable...
        return value
    }

    /**
     * Makes sure to reindex the build
     */
    override fun onPropertyChanged(entity: ProjectEntity, value: GitCommitProperty) {
        if (entity is Build) {
            gitService.collectIndexableGitCommitForBuild(entity)
        }
    }

    /**
     * Search criteria
     */
    override fun getSearchArguments(token: String): PropertySearchArguments? =
        getGitCommitSearchArguments(token)

    companion object {
        fun getGitCommitSearchArguments(token: String): PropertySearchArguments? =
            if (token.isNotBlank()) {
                PropertySearchArguments(
                    jsonContext = null,
                    jsonCriteria = "pp.json->>'commit' = :token",
                    criteriaParams = mapOf("token" to token)
                )
            } else {
                null
            }
    }
}
