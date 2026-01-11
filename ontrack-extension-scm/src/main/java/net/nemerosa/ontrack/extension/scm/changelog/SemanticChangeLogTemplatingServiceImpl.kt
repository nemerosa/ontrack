package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service

@Service
class SemanticChangeLogTemplatingServiceImpl(
    scmChangeLogService: SCMChangeLogService,
    structureService: StructureService,
    private val semanticChangelogRenderingService: SemanticChangelogRenderingService,
) : AbstractChangeLogTemplatingService<SemanticChangeLogTemplatingServiceConfig>(scmChangeLogService, structureService),
    SemanticChangeLogTemplatingService {

    override fun render(
        fromBuild: Build,
        toBuild: Build,
        config: SemanticChangeLogTemplatingServiceConfig,
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
        config: SemanticChangeLogTemplatingServiceConfig,
        suffix: String?,
        renderer: EventRenderer
    ): String {
        return semanticChangelogRenderingService.render(
            changelog = changeLog,
            config = config,
            suffix = suffix,
            renderer = renderer
        )
    }

}