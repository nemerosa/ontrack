package net.nemerosa.ontrack.extension.git.support

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.model.GitBranchConfiguration
import net.nemerosa.ontrack.extension.git.model.IndexableBuildGitCommitLink
import net.nemerosa.ontrack.extension.scm.support.TagPattern
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.model.exceptions.JsonParsingException
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.util.*

@Component
class TagPatternBuildNameGitCommitLink(
        private val structureService: StructureService
) : IndexableBuildGitCommitLink<TagPattern> {

    override val id: String = "tagPattern"

    override val name: String = "Tag pattern"

    override val form: Form = Form.create()
            .with(
                    Text.of("pattern")
                            .label("Tag pattern")
                            .help("@file:extension/git/help.net.nemerosa.ontrack.extension.git.support.TagPatternBuildNameGitCommitLink.tagPattern.tpl.html")
            )

    override fun clone(data: TagPattern, replacementFunction: (String) -> String): TagPattern {
        return data.clone(replacementFunction)
    }

    override fun getCommitFromBuild(build: Build, data: TagPattern): String {
        return data.getTagNameFromBuildName(build.name)
                .orElseThrow { BuildTagPatternExcepton(data.pattern, build.name) }
    }

    override fun parseData(node: JsonNode?): TagPattern {
        try {
            return ObjectMapperFactory.create().treeToValue(node!!, TagPattern::class.java)
        } catch (e: JsonProcessingException) {
            throw JsonParsingException(e)
        }

    }

    override fun toJson(data: TagPattern): JsonNode {
        return ObjectMapperFactory.create().valueToTree(data)
    }

    override fun getEarliestBuildAfterCommit(
            branch: Branch,
            gitClient: GitRepositoryClient,
            branchConfiguration: GitBranchConfiguration,
            data: TagPattern,
            commit: String
    ): Int? {
        return gitClient.getTagsWhichContainCommit(commit)
                // ... filter on valid tags only
                .filter { data.isValidTagName(it) }
                // ... gets the builds
                .mapNotNull { buildName -> structureService.findBuildByName(branch.project.name, branch.name, buildName).orElse(null) }
                // ... sort by ID (from the oldest build to the newest)
                .sortedBy { it.id() }
                // ... takes the first build
                .firstOrNull()
                // ... and its ID
                ?.id()
    }

    override fun getBuildNameFromTagName(tagName: String, data: TagPattern): Optional<String> {
        return data.getBuildNameFromTagName(tagName)
    }

    override fun isBuildNameValid(name: String, data: TagPattern): Boolean {
        return data.isValidTagName(name)
    }
}
