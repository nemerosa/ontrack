package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.exceptions.*
import net.nemerosa.ontrack.model.structure.*
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder

/**
 * Gives access to the structure. See [net.nemerosa.ontrack.model.structure.ProjectEntityType] for
 * the list of entities that are mapped.
 */
@RestController
@RequestMapping("/rest/structure")
class StructureController (
    structureService: StructureService
) : AbstractProjectEntityController(structureService) {
    /**
     * Project access
     */
    @GetMapping("entity/project/{project:[A-Za-z0-9\\._-]+}")
    fun project(@PathVariable project: String): Project =
        structureService.findProjectByName(project).orElseThrow { ProjectNotFoundException(project) }

    /**
     * Branch access
     */
    @GetMapping("entity/branch/{project:[A-Za-z0-9\\._-]+}/{branch:[A-Za-z0-9\\._-]+}")
    fun branch(@PathVariable project: String, @PathVariable branch: String): Branch =
        structureService.findBranchByName(project, branch)
            .orElseThrow { BranchNotFoundException(project, branch) }

    /**
     * Promotion level access
     */
    @GetMapping("entity/promotionLevel/{project:[A-Za-z0-9\\._-]+}/{branch:[A-Za-z0-9\\._-]+}/{promotionLevel:[A-Za-z0-9\\._-]+}")
    fun promotionLevel(
        @PathVariable project: String,
        @PathVariable branch: String,
        @PathVariable promotionLevel: String,
    ): PromotionLevel = structureService.findPromotionLevelByName(project, branch, promotionLevel)
        .orElseThrow { PromotionLevelNotFoundException(project, branch, promotionLevel) }

    /**
     * Validation stamp access
     */
    @GetMapping("entity/validationStamp/{project:[A-Za-z0-9\\._-]+}/{branch:[A-Za-z0-9\\._-]+}/{validationStamp:[A-Za-z0-9\\._ +-]+}")
    fun validationStamp(
        @PathVariable project: String,
        @PathVariable branch: String,
        @PathVariable validationStamp: String,
    ): ValidationStamp {
        val decodedValidationStamp: String = URLDecoder.decode(validationStamp, Charsets.UTF_8)
        return structureService.findValidationStampByName(project, branch, decodedValidationStamp)
            .orElseThrow { ValidationStampNotFoundException(project, branch, decodedValidationStamp) }
    }

    @GetMapping("entity/build/{project:[A-Za-z0-9\\._-]+}/{branch:[A-Za-z0-9\\._-]+}/{build:[A-Za-z0-9\\._-]+}")
    fun build(
        @PathVariable project: String,
        @PathVariable branch: String,
        @PathVariable build: String,
    ): Build = structureService.findBuildByName(project, branch, build)
        .orElseThrow { BuildNotFoundException(project, branch, build) }
}