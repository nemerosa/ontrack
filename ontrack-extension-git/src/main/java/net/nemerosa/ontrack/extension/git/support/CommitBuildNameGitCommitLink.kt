package net.nemerosa.ontrack.extension.git.support

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink
import net.nemerosa.ontrack.extension.git.model.GitBranchConfiguration
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.model.exceptions.JsonParsingException
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component
import java.util.regex.Pattern
import java.util.stream.Stream

@Component
class CommitBuildNameGitCommitLink : BuildGitCommitLink<CommitLinkConfig> {

    private val abbreviatedPattern = Pattern.compile("[0-9a-f]{7}")
    private val fullPattern = Pattern.compile("[0-9a-f]{40}")

    override val id: String = "commit"
    override val name: String = "Commit as name"

    override fun clone(data: CommitLinkConfig, replacementFunction: (String) -> String): CommitLinkConfig {
        return data
    }

    override fun getCommitFromBuild(build: Build, data: CommitLinkConfig): String {
        return build.name
    }

    override fun parseData(node: JsonNode?): CommitLinkConfig {
        try {
            return ObjectMapperFactory.create().treeToValue(node, CommitLinkConfig::class.java)
        } catch (e: JsonProcessingException) {
            throw JsonParsingException(e)
        }

    }

    override fun toJson(data: CommitLinkConfig): JsonNode {
        return ObjectMapperFactory.create().valueToTree(data)
    }

    override val form: Form
        get() = Form.create()
                .with(
                        YesNo.of("abbreviated")
                                .label("Abbreviated")
                                .help("Using abbreviated commit hashes or not.")
                                .value(true)
                )

    override fun getEarliestBuildAfterCommit(branch: Branch, gitClient: GitRepositoryClient, branchConfiguration: GitBranchConfiguration, data: CommitLinkConfig, commit: String): Int? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBuildCandidateReferences(commit: String, branch: Branch, gitClient: GitRepositoryClient, branchConfiguration: GitBranchConfiguration, data: CommitLinkConfig): Stream<String> {
        return if (gitClient.isCommit(commit)) {
            gitClient.log(
                    String.format("%s~1", commit),
                    gitClient.getBranchRef(branchConfiguration.branch)
            )
                    .sorted()
                    .map { gitCommit ->
                        if (data.isAbbreviated)
                            gitCommit.shortId
                        else
                            gitCommit.id
                    }
        } else {
            emptyList<String>().stream()
        }
    }

    override fun isBuildEligible(build: Build, data: CommitLinkConfig): Boolean {
        return true
    }

    override fun isBuildNameValid(name: String, data: CommitLinkConfig): Boolean {
        return if (data.isAbbreviated) {
            abbreviatedPattern.matcher(name).matches()
        } else {
            fullPattern.matcher(name).matches()
        }
    }

}
