package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.ValidationStampFilterNotShareableException;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/validation-stamp-filters")
public class ValidationStampFilterController extends AbstractResourceController {

    private final StructureService structureService;
    private final ValidationStampFilterService filterService;

    @Autowired
    public ValidationStampFilterController(StructureService structureService, ValidationStampFilterService filterService) {
        this.structureService = structureService;
        this.filterService = filterService;
    }

    @GetMapping("/branch/{branchId}/all")
    public List<ValidationStampFilter> getAllBranchValidationStampFilters(@PathVariable ID branchId) {
        return filterService.getBranchValidationStampFilters(
                structureService.getBranch(branchId),
                true
        );
    }

    @PostMapping("/branch/{branchId}/create")
    public ResponseEntity<ValidationStampFilter> createBranchValidationStampFilterForm(@PathVariable ID branchId, @RequestBody NameDescription input) {
        // Gets all validation stamp names
        List<String> vsNames = structureService.getValidationStampListForBranch(branchId).stream()
                .map(ValidationStamp::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                filterService.newValidationStampFilter(
                        new ValidationStampFilter(
                                ID.NONE,
                                input.getName(),
                                vsNames,
                                null,
                                structureService.getBranch(branchId)
                        )
                )
        );
    }

    @PutMapping("/{validationStampFilterId}/update")
    public ResponseEntity<ValidationStampFilter> updateValidationStampFilter(@PathVariable ID validationStampFilterId, @RequestBody ValidationStampFilterInput input) {
        // Changes the validation stamp filter
        ValidationStampFilter filter = filterService.getValidationStampFilter(validationStampFilterId)
                .withName(input.getName())
                .withVsNames(input.getVsNames());
        // Saves it
        filterService.saveValidationStampFilter(filter);
        // OK
        return ResponseEntity.ok(filter);
    }

    @DeleteMapping("/{validationStampFilterId}/delete")
    public ResponseEntity<Ack> deleteValidationStampFilter(@PathVariable ID validationStampFilterId) {
        return ResponseEntity.ok(
                filterService.deleteValidationStampFilter(
                        filterService.getValidationStampFilter(validationStampFilterId)
                )
        );
    }

    @PutMapping("/{validationStampFilterId}/share/project")
    public ResponseEntity<ValidationStampFilter> shareValidationStampFilterAtProject(@PathVariable ID validationStampFilterId) {
        ValidationStampFilter filter = filterService.getValidationStampFilter(validationStampFilterId);
        if (filter.getBranch() == null) {
            throw new ValidationStampFilterNotShareableException(filter.getName(), "no branch is associated with the filter");
        }
        return ResponseEntity.ok(
                filterService.shareValidationStampFilter(
                        filter,
                        filter.getBranch().getProject()
                )
        );
    }

    @PutMapping("/{validationStampFilterId}/share/global")
    public ResponseEntity<ValidationStampFilter> shareValidationStampFilterAtGlobal(@PathVariable ID validationStampFilterId) {
        ValidationStampFilter filter = filterService.getValidationStampFilter(validationStampFilterId);
        return ResponseEntity.ok(
                filterService.shareValidationStampFilter(
                        filter
                )
        );
    }

}
