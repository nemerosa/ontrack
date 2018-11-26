package net.nemerosa.ontrack.extension.git.support

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink
import net.nemerosa.ontrack.extension.git.model.GitBranchConfiguration
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.NoConfig
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Collections
import java.util.function.Function
import java.util.stream.Stream

/**
 * Build/commit link based on a [net.nemerosa.ontrack.extension.git.property.GitCommitProperty] property
 * set on the build.
 */
@Component
class GitCommitPropertyCommitLink(
        private val propertyService: PropertyService,
        private val structureService: StructureService
) : BuildGitCommitLink<NoConfig> {

    override fun getId(): String {
        return "git-commit-property"
    }

    override fun getName(): String {
        return "Git Commit Property"
    }

    override fun clone(data: NoConfig, replacementFunction: Function<String, String>): NoConfig {
        return data
    }

    override fun getCommitFromBuild(build: Build, data: NoConfig): String {
        return propertyService.getProperty(build, GitCommitPropertyType::class.java)
                .option()
                .map { it.commit }
                .orElseThrow { NoGitCommitPropertyException(build.entityDisplayName) }
    }

    override fun parseData(node: JsonNode): NoConfig {
        return NoConfig.INSTANCE
    }

    override fun toJson(data: NoConfig): JsonNode {
        return JsonUtils.`object`().end()
    }

    override fun getForm(): Form {
        return Form.create()
    }

    override fun getEarliestBuildAfterCommit(branch: Branch, gitClient: GitRepositoryClient, branchConfiguration: GitBranchConfiguration, data: NoConfig, commit: String): Int? {
        // Loops over the commits on this branch, starting from this commit
        val buildId: ID? = gitClient.forEachCommitFrom(branchConfiguration.branch, commit) { revCommit ->
            // Gets the build for this commit
            propertyService.findBuildByBranchAndSearchkey(
                    branch.id,
                    GitCommitPropertyType::class.java,
                    gitClient.getId(revCommit)
            )
        }
        return buildId?.value
    }

    override fun getBuildCandidateReferences(commit: String, branch: Branch, gitClient: GitRepositoryClient, branchConfiguration: GitBranchConfiguration, data: NoConfig): Stream<String> {
        return Stream.empty()
    }

    override fun isBuildEligible(build: Build, data: NoConfig): Boolean {
        return propertyService.hasProperty(build, GitCommitPropertyType::class.java)
    }

    /**
     * No validation for build names.
     */
    override fun isBuildNameValid(name: String, data: NoConfig): Boolean {
        return true
    }
}
