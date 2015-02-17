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
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("extension/scm")
public class SCMController extends AbstractResourceController {

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

    /**
     * Gets the list of change log file filters
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}", method = RequestMethod.GET)
    public Resources<SCMFileChangeFilter> getChangeLogFileFilters(@PathVariable ID projectId) {
        // Gets the store
        SCMFileChangeFilters config = loadStore(projectId);
        // Resources
        return Resources.of(
                config.getFilters(),
                uri(on(getClass()).getChangeLogFileFilters(projectId))
        );
    }

    /**
     * Get a change log file filter
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}/{name}", method = RequestMethod.GET)
    public Resource<SCMFileChangeFilter> getChangeLogFileFilter(@PathVariable ID projectId, @PathVariable String name) {
        SCMFileChangeFilters config = loadStore(projectId);
        // Resource
        return config.getFilters().stream()
                .filter(filter -> StringUtils.equals(name, filter.getName()))
                .findFirst()
                .map(filter ->
                                Resource.of(
                                        filter,
                                        uri(on(getClass()).getChangeLogFileFilter(projectId, name))
                                ).with("_delete", uri(on(getClass()).deleteChangeLogFileFilter(projectId, name)))
                )
                .orElseThrow(() -> new SCMFileChangeFilterNotFound(name));
    }

    private SCMFileChangeFilters loadStore(ID projectId) {
        return securityService.asAdmin(() -> {
            // Loads the project
            Project project = structureService.getProject(projectId);
            // Loads the store
            return entityDataService.retrieve(
                    project,
                    SCMFileChangeFilters.class.getName(),
                    SCMFileChangeFilters.class
            );
        }).orElse(SCMFileChangeFilters.create());
    }

    /**
     * Deletes a change log file filter
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}/{name:.*}", method = RequestMethod.DELETE)
    public Ack deleteChangeLogFileFilter(@PathVariable ID projectId, @PathVariable String name) {
        securityService.checkProjectFunction(projectId.get(), ProjectConfig.class);
        securityService.runAsAdmin(() -> entityDataService.delete(structureService.getProject(projectId), name));
        return Ack.OK;
    }

}
