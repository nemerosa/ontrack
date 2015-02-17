package net.nemerosa.ontrack.extension.scm;

import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilter;
import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilters;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.EntityDataService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("extension/scm")
public class SCMController {

    private final EntityDataService entityDataService;
    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public SCMController(EntityDataService entityDataService, StructureService structureService, SecurityService securityService) {
        this.entityDataService = entityDataService;
        this.structureService = structureService;
        this.securityService = securityService;
    }

    /**
     * Saving a change log file filter
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}", method = RequestMethod.PUT)
    public Ack saveChangeLogFileFilter(@PathVariable ID projectId, @RequestBody SCMFileChangeFilter filter) {
        securityService.checkProjectFunction(projectId.get(), ProjectConfig.class);
        return securityService.asAdmin(() -> {
            // Loads the project
            Project project = structureService.getProject(projectId);
            // Gets the store
            SCMFileChangeFilters config = entityDataService.retrieve(
                    project,
                    SCMFileChangeFilters.class.getName(),
                    SCMFileChangeFilters.class
            ).orElse(SCMFileChangeFilters.create());
            // Updates the store
            config = config.save(filter);
            // Saves the store back
            entityDataService.store(project, SCMFileChangeFilters.class.getName(), config);
            // OK
            return Ack.OK;
        });
    }

}
