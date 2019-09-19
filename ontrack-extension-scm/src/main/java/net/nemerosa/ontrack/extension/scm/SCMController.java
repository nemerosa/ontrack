package net.nemerosa.ontrack.extension.scm;

import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilter;
import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilters;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Memo;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.EntityDataService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

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
     * Gets the list of change log file filters
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}", method = RequestMethod.GET)
    public Resources<Resource<SCMFileChangeFilter>> getChangeLogFileFilters(@PathVariable ID projectId) {
        // Gets the store
        SCMFileChangeFilters config = loadStore(projectId);
        // Resources
        return Resources.of(
                config.getFilters().stream().map(f -> toResource(projectId, f)),
                uri(on(getClass()).getChangeLogFileFilters(projectId))
        )
                .with(
                        Link.CREATE,
                        uri(on(getClass()).createChangeLogFileFilterForm(projectId)),
                        securityService.isProjectFunctionGranted(projectId.get(), ProjectConfig.class)
                )
                ;
    }

    /**
     * Form to create a change log filter
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}/create", method = RequestMethod.GET)
    public Form createChangeLogFileFilterForm(@SuppressWarnings("UnusedParameters") @PathVariable ID projectId) {
        return Form.create()
                .with(Text.of("name")
                        .label("Name")
                        .help("Name to use to save the filter."))
                .with(Memo.of("patterns")
                        .label("Filter(s)")
                        .help("List of ANT-like patterns (one per line)."))
                ;
    }

    /**
     * Adding a change log file filter
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}/create", method = RequestMethod.POST)
    public Resource<SCMFileChangeFilter> createChangeLogFileFilter(@PathVariable ID projectId, @RequestBody SCMFileChangeFilter filter) {
        securityService.checkProjectFunction(projectId.get(), ProjectConfig.class);
        return securityService.asAdmin(() -> {
            // Loads the project
            Project project = structureService.getProject(projectId);
            // Gets the store
            SCMFileChangeFilters config = entityDataService.retrieve(
                    project,
                    SCMFileChangeFilters.class.getName(),
                    SCMFileChangeFilters.class
            );
            if (config == null) config = SCMFileChangeFilters.create();
            // Updates the store
            config = config.save(filter);
            // Saves the store back
            entityDataService.store(project, SCMFileChangeFilters.class.getName(), config);
            // OK
            return getChangeLogFileFilter(projectId, filter.getName());
        });
    }

    /**
     * Updating form for a change log file filter
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}/{name}/update", method = RequestMethod.GET)
    public Form saveChangeLogFileFilterForm(@PathVariable ID projectId, @PathVariable String name) {
        Resource<SCMFileChangeFilter> filter = getChangeLogFileFilter(projectId, name);
        return Form.create()
                .with(Text.of("name")
                        .label("Name")
                        .help("Name to use to save the filter.")
                        .readOnly()
                        .value(filter.getData().getName()))
                .with(Memo.of("patterns")
                        .label("Filter(s)")
                        .help("List of ANT-like patterns (one per line).")
                        .value(filter.getData().getPatterns().stream().collect(Collectors.joining("\n"))))
                ;
    }

    /**
     * Updating a change log file filter
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}/{name}/update", method = RequestMethod.PUT)
    public Resource<SCMFileChangeFilter> saveChangeLogFileFilter(@PathVariable ID projectId, @PathVariable String name, @RequestBody SCMFileChangeFilter filter) {
        if (!StringUtils.equals(name, filter.getName())) {
            throw new IllegalStateException("The name of the filter in the request body must match the one in the URL");
        }
        return createChangeLogFileFilter(projectId, filter);
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
                .map(filter -> toResource(projectId, filter))
                .orElseThrow(() -> new SCMFileChangeFilterNotFound(name));
    }

    private Resource<SCMFileChangeFilter> toResource(ID projectId, SCMFileChangeFilter filter) {
        boolean granted = securityService.isProjectFunctionGranted(projectId.get(), ProjectConfig.class);
        return Resource.of(
                filter,
                uri(on(getClass()).getChangeLogFileFilter(projectId, filter.getName()))
        )
                .with(
                        Link.DELETE,
                        uri(on(getClass()).deleteChangeLogFileFilter(projectId, filter.getName())),
                        granted
                )
                .with(
                        Link.UPDATE,
                        uri(on(getClass()).saveChangeLogFileFilterForm(projectId, filter.getName())),
                        granted
                )
                ;
    }

    private SCMFileChangeFilters loadStore(ID projectId) {
        return securityService.asAdmin(() -> {
            // Loads the project
            Project project = structureService.getProject(projectId);
            // Loads the store
            SCMFileChangeFilters config = entityDataService.retrieve(
                    project,
                    SCMFileChangeFilters.class.getName(),
                    SCMFileChangeFilters.class
            );
            if (config == null) config = SCMFileChangeFilters.create();
            return config;
        });
    }

    /**
     * Deletes a change log file filter
     */
    @RequestMapping(value = "changeLog/fileFilter/{projectId}/{name}/delete", method = RequestMethod.DELETE)
    public Ack deleteChangeLogFileFilter(@PathVariable ID projectId, @PathVariable String name) {
        securityService.checkProjectFunction(projectId.get(), ProjectConfig.class);
        securityService.asAdmin(() ->
                        entityDataService.withData(
                                structureService.getProject(projectId),
                                SCMFileChangeFilters.class.getName(),
                                SCMFileChangeFilters.class,
                                (SCMFileChangeFilters filters) -> filters.remove(name)
                        )
        );
        return Ack.OK;
    }

}
