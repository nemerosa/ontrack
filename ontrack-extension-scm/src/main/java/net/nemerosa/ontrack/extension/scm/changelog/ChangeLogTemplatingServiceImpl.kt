package net.nemerosa.ontrack.extension.scm.changelog

import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.EntityDisplayNameService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.render
import net.nemerosa.ontrack.model.templating.getBooleanTemplatingParam
import net.nemerosa.ontrack.model.templating.getListStringsTemplatingParam
import org.springframework.stereotype.Service

@Service
class ChangeLogTemplatingServiceImpl(
    private val scmChangeLogService: SCMChangeLogService,
    private val entityDisplayNameService: EntityDisplayNameService,
    private val structureService: StructureService,
) : ChangeLogTemplatingService {

    override fun render(
        fromBuild: Build,
        toBuild: Build,
        configMap: Map<String, String>,
        renderer: EventRenderer
    ): String {

        val empty = ChangeLogTemplatingServiceConfig.emptyValue(configMap)
        val title =
            configMap.getBooleanTemplatingParam(ChangeLogTemplatingServiceConfig::title.name, false)
        val dependencies =
            configMap.getListStringsTemplatingParam(ChangeLogTemplatingServiceConfig::dependencies.name)
                ?: emptyList()
        val allQualifiers = configMap.getBooleanTemplatingParam(
            ChangeLogTemplatingServiceConfig::allQualifiers.name,
            false
        )
        val defaultQualifierFallback = configMap.getBooleanTemplatingParam(
            ChangeLogTemplatingServiceConfig::defaultQualifierFallback.name,
            false
        )

        return if (allQualifiers && dependencies.isNotEmpty()) {
            renderAllQualifiers(
                fromBuild,
                toBuild,
                dependencies,
                defaultQualifierFallback,
                renderer,
                empty,
                title
            )
        } else {
            // Single change log
            renderChangeLog(fromBuild, toBuild, dependencies, defaultQualifierFallback, renderer, empty, title)
        }
    }

    private fun renderAllQualifiers(
        fromBuild: Build,
        toBuild: Build,
        dependencies: List<String>,
        defaultQualifierFallback: Boolean,
        renderer: EventRenderer,
        empty: String,
        title: Boolean,
    ): String {
        // Gets the change log boundaries but for the last dependency
        val parsedDependencies = dependencies.map { DependencyLink.parse(it) }
        val firstDependencies = parsedDependencies.dropLast(1)
        val lastDependencyProject = parsedDependencies.last().project
        val (deepFrom, deepTo) = runBlocking {
            scmChangeLogService.getChangeLogBoundaries(
                from = fromBuild,
                to = toBuild,
                dependencies = firstDependencies,
                defaultQualifierFallback = defaultQualifierFallback,
            )
        } ?: return ""

        // Gets all links to the last dependency project and all the common qualifiers
        // Including the default qualifier
        val qualifiers = collectAllQualifiers(lastDependencyProject, deepFrom, deepTo)

        // For each qualifier (including the default one), get a change log
        val qualifiedChangeLogs = runBlocking {
            qualifiers.associateWith { qualifier ->
                scmChangeLogService.getChangeLog(
                    from = deepFrom,
                    to = deepTo,
                    dependencies = listOf(
                        DependencyLink(
                            project = lastDependencyProject,
                            qualifier = qualifier,
                        )
                    ),
                    defaultQualifierFallback = defaultQualifierFallback,
                )
            }
        }

        // Renders as a list
        return qualifiedChangeLogs.entries
            .filter { (_, changeLog) -> changeLog != null && !changeLog.isEmpty() }
            .joinToString(
                separator = renderer.renderSpace()
            ) { (qualifier, changeLog) ->
                check(changeLog != null) {
                    "At this stage, the change log cannot be null"
                }
                renderChangeLog(
                    changeLog = changeLog,
                    renderer = renderer,
                    empty = empty,
                    title = title,
                    projectNameSuffix = if (qualifier.isBlank()) {
                        ""
                    } else {
                        " [$qualifier]"
                    }
                )
            }
    }

    private fun collectAllQualifiers(projectName: String, vararg builds: Build): List<String> {
        val qualifiers = mutableSetOf<String>()
        for (build in builds) {
            val linkQualifiers = structureService.getQualifiedBuildsUsedBy(build, size = 100) {
                it.build.project.name == projectName
            }.pageItems.map { it.qualifier }.filter { it.isNotBlank() }.toSet()
            qualifiers += linkQualifiers
        }
        // Sorting the qualifiers
        val sortedQualifiers = qualifiers.sorted().toMutableList()
        // Always adding the default qualifier
        sortedQualifiers.add(0, "")
        // OK
        return sortedQualifiers.toList()
    }

    private fun renderChangeLog(
        fromBuild: Build,
        toBuild: Build,
        dependencies: List<String>,
        defaultQualifierFallback: Boolean,
        renderer: EventRenderer,
        empty: String,
        title: Boolean
    ): String {
        // ... getting the change log, recursively or not
        val changeLog = runBlocking {
            scmChangeLogService.getChangeLog(
                fromBuild,
                toBuild,
                dependencies.map { DependencyLink.parse(it) },
                defaultQualifierFallback,
            )
        }
        return renderChangeLog(changeLog, renderer, empty, title)
    }

    private fun renderChangeLog(
        changeLog: SCMChangeLog?,
        renderer: EventRenderer,
        empty: String,
        title: Boolean,
        projectNameSuffix: String = "",
    ): String {
        // Rendered change log
        val renderedChangeLog = changeLog
            ?.takeIf { it.from.id() != it.to.id() }
            ?.let { scmChangeLog ->
                renderChangeLog(
                    changeLog = scmChangeLog,
                    renderer = renderer
                )
            } ?: empty
        // Title?
        return if (title) {
            if (changeLog != null) {

                val projectName = entityDisplayNameService.render(changeLog.from.project, renderer)
                val fromName = entityDisplayNameService.render(changeLog.from, renderer)
                val toName = entityDisplayNameService.render(changeLog.to, renderer)

                val titleText = if (changeLog.from.id() != changeLog.to.id()) {
                    """
                            Change log for $projectName$projectNameSuffix from $fromName to $toName
                        """.trimIndent()

                } else {
                    """
                            Project $projectName$projectNameSuffix version $fromName
                        """.trimIndent()
                }

                renderer.renderSection(
                    title = titleText,
                    content = renderedChangeLog,
                )
            } else {
                // Change log not defined, and still asking for a title without a reference
                // Skipping
                ""
            }
        } else {
            renderedChangeLog
        }
    }

    private fun renderChangeLog(changeLog: SCMChangeLog, renderer: EventRenderer): String {
        return renderer.renderList(
            changeLog.issues?.issues?.map { issue ->
                val link = renderer.renderLink(
                    text = issue.displayKey,
                    href = issue.url,
                )
                val text = issue.summary
                "$link $text"
            } ?: emptyList()
        )
    }

}