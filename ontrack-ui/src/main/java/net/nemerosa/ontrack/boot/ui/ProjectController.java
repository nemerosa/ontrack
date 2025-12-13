package net.nemerosa.ontrack.boot.ui;

import jakarta.validation.Valid;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.annotations.API;
import net.nemerosa.ontrack.model.annotations.APIMethod;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/structure/projects")
@API("Management of projects")
public class ProjectController {

    private final StructureService structureService;
    private final CopyService copyService;
    private final ProjectFavouriteService projectFavouriteService;

    @Autowired
    public ProjectController(StructureService structureService, CopyService copyService, ProjectFavouriteService projectFavouriteService) {
        this.structureService = structureService;
        this.copyService = copyService;
        this.projectFavouriteService = projectFavouriteService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Project> getProjectList() {
        return structureService.getProjectList();
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Project> newProject(@RequestBody @Valid NameDescriptionState nameDescription) {
        // Creates a new project instance
        Project project = Project.of(nameDescription);
        // Saves it into the repository
        project = structureService.newProject(project);
        // OK
        return ResponseEntity.ok(project);
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.GET)
    public ResponseEntity<Project> getProject(@PathVariable ID projectId) {
        return ResponseEntity.ok(structureService.getProject(projectId));
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.DELETE)
    public ResponseEntity<Ack> deleteProject(@PathVariable ID projectId) {
        return ResponseEntity.ok(structureService.deleteProject(projectId));
    }

    @RequestMapping(value = "{projectId}/update", method = RequestMethod.PUT)
    @APIMethod(value = "Updates project")
    public ResponseEntity<Project> saveProject(@PathVariable ID projectId, @RequestBody @Valid NameDescriptionState nameDescription) {
        // Gets from the repository
        Project project = structureService.getProject(projectId);
        // Updates
        project = project.update(nameDescription);
        // Saves in repository
        structureService.saveProject(project);
        // As resource
        return ResponseEntity.ok(project);
    }

    @RequestMapping(value = "{projectId}/enable", method = RequestMethod.PUT)
    public ResponseEntity<Project> enableProject(@PathVariable ID projectId) {
        // Gets from the repository
        Project project = structureService.getProject(projectId);
        // Saves in repository
        return ResponseEntity.ok(structureService.enableProject(project));
    }

    @RequestMapping(value = "{projectId}/disable", method = RequestMethod.PUT)
    public ResponseEntity<Project> disableProject(@PathVariable ID projectId) {
        // Gets from the repository
        Project project = structureService.getProject(projectId);
        // Saves in repository
        return ResponseEntity.ok(structureService.disableProject(project));
    }

    /**
     * Clones this project into another one.
     */
    @RequestMapping(value = "{projectId}/clone", method = RequestMethod.POST)
    public ResponseEntity<Project> clone(@PathVariable ID projectId, @RequestBody ProjectCloneRequest request) {
        // Gets the project
        Project project = structureService.getProject(projectId);
        // Performs the clone
        return ResponseEntity.ok(copyService.cloneProject(project, request));
    }

    @RequestMapping(value = "{projectId}/favourite", method = RequestMethod.PUT)
    public ResponseEntity<Project> favouriteProject(@PathVariable ID projectId) {
        Project project = structureService.getProject(projectId);
        projectFavouriteService.setProjectFavourite(project, true);
        return ResponseEntity.ok(project);
    }

    @RequestMapping(value = "{projectId}/unfavourite", method = RequestMethod.PUT)
    public ResponseEntity<Project> unfavouriteProject(@PathVariable ID projectId) {
        Project project = structureService.getProject(projectId);
        projectFavouriteService.setProjectFavourite(project, false);
        return ResponseEntity.ok(project);
    }

}
