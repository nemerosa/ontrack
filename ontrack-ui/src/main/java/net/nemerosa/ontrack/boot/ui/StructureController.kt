package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.exceptions.*
import net.nemerosa.ontrack.model.structure.*
import org.springframework.web.bind.annotation.*

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
    @GetMapping("entity/project/{project:.*}")
    fun project(@PathVariable project: String): Project =
        structureService.findProjectByName(project).orElseThrow { ProjectNotFoundException(project) }

    /**
     * Branch access
     */
    @GetMapping("entity/branch/{project}/{branch:.*}")
    fun branch(@PathVariable project: String, @PathVariable branch: String): Branch =
        structureService.findBranchByName(project, branch)
            .orElseThrow { BranchNotFoundException(project, branch) }

    /**
     * Promotion level access
     */
    @GetMapping("entity/promotionLevel/{project}/{branch}/{promotionLevel:.*}")
    fun promotionLevel(
        @PathVariable project: String,
        @PathVariable branch: String,
        @PathVariable promotionLevel: String,
    ): PromotionLevel = structureService.findPromotionLevelByName(project, branch, promotionLevel)
        .orElseThrow { PromotionLevelNotFoundException(project, branch, promotionLevel) }

    /**
     * Validation stamp access
     */
    @GetMapping("entity/validationStamp/{project}/{branch}/{validationStamp:.*}")
    fun validationStamp(
        @PathVariable project: String,
        @PathVariable branch: String,
        @PathVariable validationStamp: String,
    ): ValidationStamp = structureService.findValidationStampByName(project, branch, validationStamp)
        .orElseThrow { ValidationStampNotFoundException(project, branch, validationStamp) }

    @GetMapping("entity/build/{project}/{branch}/{build:.*}")
    fun build(
        @PathVariable project: String,
        @PathVariable branch: String,
        @PathVariable build: String,
    ): Build = structureService.findBuildByName(project, branch, build)
        .orElseThrow { BuildNotFoundException(project, branch, build) }
}