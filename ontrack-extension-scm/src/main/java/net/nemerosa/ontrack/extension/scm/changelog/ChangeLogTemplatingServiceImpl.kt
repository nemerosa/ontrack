package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.events.renderWithSpace
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.EntityDisplayNameService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.render
import net.nemerosa.ontrack.model.templating.getBooleanTemplatingParam
import net.nemerosa.ontrack.model.templating.getListStringsTemplatingParam
import org.springframework.stereotype.Service

@Service
class ChangeLogTemplatingServiceImpl(
    scmChangeLogService: SCMChangeLogService,
    private val entityDisplayNameService: EntityDisplayNameService,
    structureService: StructureService,
) : AbstractChangeLogTemplatingService<ChangeLogTemplatingServiceConfig>(scmChangeLogService, structureService),
    ChangeLogTemplatingService {

    @Deprecated("Use the typed method, without the config map.")
    override fun render(
        fromBuild: Build,
        toBuild: Build,
        configMap: Map<String, String>,
        renderer: EventRenderer
    ): String {

        val config = ChangeLogTemplatingServiceConfig(
            empty = ChangeLogTemplatingServiceConfig.emptyValue(configMap),
            title = configMap.getBooleanTemplatingParam(ChangeLogTemplatingServiceConfig::title.name, false),
            dependencies = configMap.getListStringsTemplatingParam(ChangeLogTemplatingServiceConfig::dependencies.name)
                ?: emptyList(),
            allQualifiers = configMap.getBooleanTemplatingParam(
                ChangeLogTemplatingServiceConfig::allQualifiers.name,
                false
            ),
            defaultQualifierFallback = configMap.getBooleanTemplatingParam(
                ChangeLogTemplatingServiceConfig::defaultQualifierFallback.name,
                false
            ),
            commitsOption = configMap[ChangeLogTemplatingServiceConfig::commitsOption.name]
                ?.let { ChangeLogTemplatingCommitsOption.valueOf(it) }
                ?: ChangeLogTemplatingCommitsOption.NONE,
        )

        return render(
            fromBuild = fromBuild,
            toBuild = toBuild,
            config = config,
            renderer = renderer,
        )
    }

    override fun render(
        fromBuild: Build,
        toBuild: Build,
        config: ChangeLogTemplatingServiceConfig,
        renderer: EventRenderer
    ): String {
        return render(
            fromBuild = fromBuild,
            toBuild = toBuild,
            allQualifiers = config.allQualifiers,
            dependencies = config.dependencies,
            defaultQualifierFallback = config.defaultQualifierFallback,
            config = config,
            renderer = renderer,
        )
    }

    override fun renderChangeLog(
        changeLog: SCMChangeLog,
        config: ChangeLogTemplatingServiceConfig,
        suffix: String?,
        renderer: EventRenderer
    ): String {
        // Rendered change log
        val renderedChangeLog = changeLog
            .takeIf { it.from.id() != it.to.id() }
            ?.let { scmChangeLog ->
                renderChangeLogItems(
                    changeLog = scmChangeLog,
                    commitsOption = config.commitsOption,
                    renderer = renderer
                )
            } ?: config.empty
        // Title?
        return if (config.title) {
            run {

                val projectName = entityDisplayNameService.render(changeLog.from.project, renderer)
                val fromName = entityDisplayNameService.render(changeLog.from, renderer)
                val toName = entityDisplayNameService.render(changeLog.to, renderer)

                val titleText = if (changeLog.from.id() != changeLog.to.id()) {
                    """
                                    Change log for $projectName$suffix from $fromName to $toName
                                """.trimIndent()

                } else {
                    """
                                    Project $projectName$suffix version $fromName
                                """.trimIndent()
                }

                renderer.renderSection(
                    title = titleText,
                    content = renderedChangeLog,
                )
            }
        } else {
            renderedChangeLog
        }
    }

    private fun renderChangeLogItems(
        changeLog: SCMChangeLog,
        commitsOption: ChangeLogTemplatingCommitsOption,
        renderer: EventRenderer,
    ): String {
        // Issues
        val hasIssues = changeLog.issues?.issues != null && changeLog.issues.issues.isNotEmpty()
        val issues = renderChangeLogIssues(renderer, changeLog)
        // Commits
        val commits: String by lazy { renderChangeLogCommits(changeLog, renderer) }
        // Everything together
        return when (commitsOption) {
            ChangeLogTemplatingCommitsOption.NONE -> issues
            ChangeLogTemplatingCommitsOption.OPTIONAL -> if (hasIssues) {
                issues
            } else {
                commits
            }

            ChangeLogTemplatingCommitsOption.ALWAYS ->
                renderer.renderWithSpace(issues, "Commits:", commits)
        }
    }

    private fun renderChangeLogCommits(
        changeLog: SCMChangeLog,
        renderer: EventRenderer
    ): String = renderer.renderList(
        changeLog.commits.map { commit ->
            renderChangeLogCommit(commit.commit, renderer)
        }
    )

    private fun renderChangeLogCommit(commit: SCMCommit, renderer: EventRenderer): String {
        val link = renderer.renderLink(
            text = commit.shortId,
            href = commit.link,
        )
        return "$link ${commit.message}"
    }

}