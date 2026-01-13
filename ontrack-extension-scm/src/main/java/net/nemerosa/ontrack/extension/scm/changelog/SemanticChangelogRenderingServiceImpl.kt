package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.events.EventRenderer
import org.springframework.stereotype.Service

@Service
class SemanticChangelogRenderingServiceImpl(
    private val semanticChangelogService: SemanticChangelogService,
) : SemanticChangelogRenderingService {

    companion object {
        private val types = mapOf(
            "build" to "Build",
            "chore" to "Misc.",
            "ci" to "CI",
            "docs" to "Documentation",
            "feat" to "Features",
            "fix" to "Fixes",
            "style" to "Style",
            "refactor" to "Refactoring",
            "perf" to "Performance",
            "test" to "Tests",
        )
    }

    override fun render(
        changelog: SCMChangeLog,
        config: SemanticChangeLogConfig,
        suffix: String?,
        renderer: EventRenderer
    ): String {
        val entries = changelog.commits.map {
            semanticChangelogService.parseSemanticCommit(it.commit.message)
        }

        val grouped = entries
            .filter { !it.type.isNullOrBlank() }
            .filter { it.type !in config.exclude }
            .groupBy { it.type!! }
            .mapKeys { (type, _) -> getTypeTitle(type, config) }
            .toSortedMap()

        return buildString {

            val hasIssues = changelog.issues?.issues != null && changelog.issues.issues.isNotEmpty()
            if (hasIssues) {
                val issues = renderChangeLogIssues(renderer, changelog)
                append("Issues:\n\n").append(issues).append("\n")
            }

            var no = 0
            grouped.forEach { (title, commits) ->
                if (no++ > 0 || hasIssues) {
                    append("\n")
                }

                val content = buildString {
                    append(
                        renderer.renderList(
                            commits.map { commit ->
                                renderCommit(commit, renderer)
                            }
                        )
                    )
                }

                append("$title:").append("\n\n").append(content).append("\n")
            }
        }
    }

    fun renderCommit(
        commit: SemanticCommit,
        renderer: EventRenderer
    ): String {
        return buildString {
            if (!commit.scope.isNullOrBlank()) {
                append(renderer.renderStrong(commit.scope))
                append(" - ")
            }
            append(commit.subject)
        }
    }

    fun getTypeTitle(type: String, config: SemanticChangeLogConfig): String {
        return config.sections.find { it.type == type }
            ?.title
            ?: types[type]
            ?: type
    }

}