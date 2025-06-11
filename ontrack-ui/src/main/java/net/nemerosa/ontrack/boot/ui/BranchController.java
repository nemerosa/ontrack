package net.nemerosa.ontrack.boot.ui;

import jakarta.validation.Valid;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/structure")
public class BranchController {

    private final StructureService structureService;
    private final CopyService copyService;
    private final BranchFavouriteService branchFavouriteService;

    @Autowired
    public BranchController(
            StructureService structureService,
            CopyService copyService,
            BranchFavouriteService branchFavouriteService) {
        this.structureService = structureService;
        this.copyService = copyService;
        this.branchFavouriteService = branchFavouriteService;
    }

    @RequestMapping(value = "projects/{projectId}/branches", method = RequestMethod.GET)
    public List<Branch> getBranchListForProject(@PathVariable ID projectId) {
        return structureService.getBranchesForProject(projectId);
    }

    @RequestMapping(value = "projects/{projectId}/branches/create", method = RequestMethod.POST)
    public ResponseEntity<Branch> newBranch(@PathVariable ID projectId, @RequestBody @Valid NameDescriptionState nameDescription) {
        // Gets the project
        Project project = structureService.getProject(projectId);
        // Creates a new branch instance
        Branch branch = Branch.of(project, nameDescription);
        // Saves it into the repository
        branch = structureService.newBranch(branch);
        // OK
        return ResponseEntity.ok(branch);
    }

    @RequestMapping(value = "branches/{branchId}", method = RequestMethod.GET)
    public ResponseEntity<Branch> getBranch(@PathVariable ID branchId) {
        return ResponseEntity.ok(structureService.getBranch(branchId));
    }

    @RequestMapping(value = "branches/{branchId}", method = RequestMethod.DELETE)
    public ResponseEntity<Ack> deleteBranch(@PathVariable ID branchId) {
        return ResponseEntity.ok(structureService.deleteBranch(branchId));
    }

    @RequestMapping(value = "branches/{branchId}/update", method = RequestMethod.PUT)
    public ResponseEntity<Branch> updateBranch(@PathVariable ID branchId, @RequestBody @Valid NameDescriptionState form) {
        // Loads and updates branch
        Branch branch = structureService.getBranch(branchId).update(form);
        // Saves the branch
        structureService.saveBranch(branch);
        // OK
        return ResponseEntity.ok(branch);
    }

    @RequestMapping(value = "branches/{branchId}/enable", method = RequestMethod.PUT)
    public ResponseEntity<Branch> enableBranch(@PathVariable ID branchId) {
        // Loads and updates branch
        Branch branch = structureService.getBranch(branchId);
        // Saves the branch
        return ResponseEntity.ok(structureService.enableBranch(branch));
    }

    @RequestMapping(value = "branches/{branchId}/disable", method = RequestMethod.PUT)
    public ResponseEntity<Branch> disableBranch(@PathVariable ID branchId) {
        // Loads and updates branch
        Branch branch = structureService.getBranch(branchId).withDisabled(true);
        // Disables the branch
        return ResponseEntity.ok(structureService.disableBranch(branch));
    }

    /**
     * Copies the configuration from a branch into this one.
     */
    @RequestMapping(value = "branches/{branchId}/copy", method = RequestMethod.PUT)
    public ResponseEntity<Branch> copy(@PathVariable ID branchId, @RequestBody BranchCopyRequest request) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Performs the copy
        return ResponseEntity.ok(copyService.copy(branch, request));
    }

    /**
     * Bulk update for a branch.
     */
    @RequestMapping(value = "branches/{branchId}/update/bulk", method = RequestMethod.PUT)
    public ResponseEntity<Branch> bulkUpdate(@PathVariable ID branchId, @RequestBody BranchBulkUpdateRequest request) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Performs the update
        return ResponseEntity.ok(copyService.update(branch, request));
    }

    /**
     * Clones this branch into another one.
     */
    @RequestMapping(value = "branches/{branchId}/clone", method = RequestMethod.POST)
    public ResponseEntity<Branch> clone(@PathVariable ID branchId, @RequestBody BranchCloneRequest request) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Performs the clone
        return ResponseEntity.ok(copyService.cloneBranch(branch, request));
    }

    @RequestMapping(value = "branches/{branchId}/favourite", method = RequestMethod.PUT)
    public ResponseEntity<Branch> favouriteBranch(@PathVariable ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        branchFavouriteService.setBranchFavourite(branch, true);
        return ResponseEntity.ok(branch);
    }

    @RequestMapping(value = "branches/{branchId}/unfavourite", method = RequestMethod.PUT)
    public ResponseEntity<Branch> unfavouriteBranch(@PathVariable ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        branchFavouriteService.setBranchFavourite(branch, false);
        return ResponseEntity.ok(branch);
    }

}
