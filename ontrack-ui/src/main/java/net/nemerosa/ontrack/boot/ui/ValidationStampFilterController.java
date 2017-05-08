package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.ValidationStampFilterNotShareableException;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiSelection;
import net.nemerosa.ontrack.model.form.MultiStrings;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.SelectableString;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/validation-stamp-filters")
public class ValidationStampFilterController extends AbstractResourceController {

    private final StructureService structureService;
    private final ValidationStampFilterService filterService;

    @Autowired
    public ValidationStampFilterController(StructureService structureService, ValidationStampFilterService filterService) {
        this.structureService = structureService;
        this.filterService = filterService;
    }

    @GetMapping("/branch/{branchId}/all")
    public Resources<ValidationStampFilter> getAllBranchValidationStampFilters(@PathVariable ID branchId) {
        return Resources.of(
                filterService.getBranchValidationStampFilters(
                        structureService.getBranch(branchId),
                        true
                ),
                uri(on(getClass()).getAllBranchValidationStampFilters(branchId))
        );
    }

    @GetMapping("/branch/{branchId}/create")
    public Form getBranchValidationStampFilterForm(@SuppressWarnings("unused") @PathVariable ID branchId) {
        return Form.create().with(
                Text.of("name")
                        .label("Name")
                        .length(40)
        );
    }

    @PostMapping("/branch/{branchId}/create")
    public ValidationStampFilter createBranchValidationStampFilterForm(@PathVariable ID branchId, @RequestBody NameDescription input) {
        // Gets all validation stamp names
        List<String> vsNames = structureService.getValidationStampListForBranch(branchId).stream()
                .map(ValidationStamp::getName)
                .collect(Collectors.toList());
        return filterService.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(input.getName())
                        .branch(structureService.getBranch(branchId))
                        .vsNames(vsNames)
                        .build()
        );
    }

    @GetMapping("/{validationStampFilterId}/update")
    public Form getValidationStampFilterUpdateForm(@PathVariable ID validationStampFilterId) {
        // Gets the validation stamp filter
        ValidationStampFilter filter = filterService.getValidationStampFilter(validationStampFilterId);
        // Base form (name)
        Form form = Form.create().name().name(filter.getName());
        // Scope
        ValidationStampFilterScope scope = filter.getScope();

        /*
         * For a branch filter, we edit using the list of validation stamps of the branch.
         * For a project filter, we edit using the list of validation stamps of all the branches.
         * For a global filter, we edit using a list of strings.
         */
        if (scope == ValidationStampFilterScope.BRANCH) {
            form = form.with(
                    MultiSelection.of("vsNames")
                            .label("Validation stamps")
                            .items(
                                    structureService.getValidationStampListForBranch(filter.getBranch().getId()).stream()
                                            .map(ValidationStamp::getName)
                                            .map(name -> SelectableString.of(name, filter.getVsNames()))
                                            .collect(Collectors.toList())
                            )
            );
        } else if (scope == ValidationStampFilterScope.PROJECT) {
            form = form.with(
                    MultiSelection.of("vsNames")
                            .label("Validation stamps")
                            .items(
                                    structureService.getBranchesForProject(filter.getProject().getId()).stream()
                                            .flatMap(branch -> structureService.getValidationStampListForBranch(branch.getId()).stream())
                                            .map(ValidationStamp::getName)
                                            .distinct()
                                            .map(name -> SelectableString.of(name, filter.getVsNames()))
                                            .collect(Collectors.toList())
                            )
            );
        } else {
            form = form.with(
                    MultiStrings.of("vsNames")
                            .label("Validation stamps")
                            .value(filter.getVsNames())
            );
        }

        // OK
        return form;
    }

    @PutMapping("/{validationStampFilterId}/update")
    public ValidationStampFilter updateValidationStampFilter(@PathVariable ID validationStampFilterId, @RequestBody ValidationStampFilterInput input) {
        // Changes the validation stamp filter
        ValidationStampFilter filter = filterService.getValidationStampFilter(validationStampFilterId)
                .withName(input.getName())
                .withVsNames(input.getVsNames());
        // Saves it
        filterService.saveValidationStampFilter(filter);
        // OK
        return filter;
    }

    @DeleteMapping("/{validationStampFilterId}/delete")
    public Ack deleteValidationStampFilter(@PathVariable ID validationStampFilterId) {
        return filterService.deleteValidationStampFilter(
                filterService.getValidationStampFilter(validationStampFilterId)
        );
    }

    @PutMapping("/{validationStampFilterId}/share/project")
    public ValidationStampFilter shareValidationStampFilterAtProject(@PathVariable ID validationStampFilterId) {
        ValidationStampFilter filter = filterService.getValidationStampFilter(validationStampFilterId);
        if (filter.getBranch() == null) {
            throw new ValidationStampFilterNotShareableException(filter.getName(), "no branch is associated with the filter");
        }
        return filterService.shareValidationStampFilter(
                filter,
                filter.getBranch().getProject()
        );
    }

    @PutMapping("/{validationStampFilterId}/share/global")
    public ValidationStampFilter shareValidationStampFilterAtGlobal(@PathVariable ID validationStampFilterId) {
        ValidationStampFilter filter = filterService.getValidationStampFilter(validationStampFilterId);
        return filterService.shareValidationStampFilter(
                filter
        );
    }

}
