package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.exceptions.*;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gives access to the structure. See {@link net.nemerosa.ontrack.model.structure.ProjectEntityType} for
 * the list of entities that are mapped.
 */
@RestController
@RequestMapping("/rest/structure")
public class StructureController extends AbstractProjectEntityController {

    @Autowired
    public StructureController(StructureService structureService) {
        super(structureService);
    }

    /**
     * Project access
     */
    @RequestMapping(value = "entity/project/{project:.*}", method = RequestMethod.GET)
    public Project project(@PathVariable String project) {
        return structureService.findProjectByName(project).orElseThrow(() -> new ProjectNotFoundException(project));
    }

    /**
     * Branch access
     */
    @RequestMapping(value = "entity/branch/{project}/{branch:.*}", method = RequestMethod.GET)
    public Branch branch(@PathVariable String project, @PathVariable String branch) {
        return structureService.findBranchByName(project, branch).orElseThrow(() -> new BranchNotFoundException(project, branch));
    }

    /**
     * Promotion level access
     */
    @RequestMapping(value = "entity/promotionLevel/{project}/{branch}/{promotionLevel:.*}", method = RequestMethod.GET)
    public PromotionLevel promotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
        return structureService.findPromotionLevelByName(project, branch, promotionLevel)
                .orElseThrow(() -> new PromotionLevelNotFoundException(project, branch, promotionLevel));
    }

    /**
     * Validation stamp access
     */
    @RequestMapping(value = "entity/validationStamp/{project}/{branch}/{validationStamp:.*}", method = RequestMethod.GET)
    public ValidationStamp validationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        return structureService.findValidationStampByName(project, branch, validationStamp)
                .orElseThrow(() -> new ValidationStampNotFoundException(project, branch, validationStamp));
    }

    @RequestMapping(value = "entity/build/{project}/{branch}/{build:.*}", method = RequestMethod.GET)
    public Build build(@PathVariable String project, @PathVariable String branch, @PathVariable String build) {
        return structureService.findBuildByName(project, branch, build)
                .orElseThrow(() -> new BuildNotFoundException(project, branch, build));
    }

}
