package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
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
@RequestMapping("/structure")
public class StructureController extends AbstractProjectEntityController {

    @Autowired
    public StructureController(StructureService structureService) {
        super(structureService);
    }

    /**
     * Project access
     */
    @RequestMapping(value = "entity/project/{project}", method = RequestMethod.GET)
    public Project project(@PathVariable String project) {
        return structureService.findProjectByName(project);
    }

    /**
     * Branch access
     */
    @RequestMapping(value = "entity/branch/{project}/{branch}", method = RequestMethod.GET)
    public Branch branch(@PathVariable String project, @PathVariable String branch) {
        return structureService.findBranchByName(project, branch);
    }

    // FIXME PROMOTION_LEVEL

    // FIXME VALIDATION_STAMP

    // FIXME BUILD

    // FIXME PROMOTION_RUN

    // FIXME VALIDATION_RUN

}
