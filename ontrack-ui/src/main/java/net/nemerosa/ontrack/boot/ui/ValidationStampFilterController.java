package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiStrings;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

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

    @GetMapping("/global")
    public Resources<ValidationStampFilter> getGlobalValidationStampFilters() {
        return Resources.of(
                filterService.getGlobalValidationStampFilters(),
                uri(on(getClass()).getGlobalValidationStampFilters())
        );
    }

    @GetMapping("/project/{projectId}")
    public Resources<ValidationStampFilter> getProjectValidationStampFilters(@PathVariable ID projectId) {
        return Resources.of(
                filterService.getProjectValidationStampFilters(
                        structureService.getProject(projectId),
                        false
                ),
                uri(on(getClass()).getProjectValidationStampFilters(projectId))
        );
    }

    @GetMapping("/project/{projectId}/all")
    public Resources<ValidationStampFilter> getAllProjectValidationStampFilters(@PathVariable ID projectId) {
        return Resources.of(
                filterService.getProjectValidationStampFilters(
                        structureService.getProject(projectId),
                        true
                ),
                uri(on(getClass()).getAllProjectValidationStampFilters(projectId))
        );
    }

    @GetMapping("/branch/{branchId}")
    public Resources<ValidationStampFilter> getBranchValidationStampFilters(@PathVariable ID branchId) {
        return Resources.of(
                filterService.getBranchValidationStampFilters(
                        structureService.getBranch(branchId),
                        false
                ),
                uri(on(getClass()).getBranchValidationStampFilters(branchId))
        );
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
    public ValidationStampFilter createBranchValidationStampFilterForm(@SuppressWarnings("unused") @PathVariable ID branchId, @RequestBody NameDescription input) {
        return filterService.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(input.getName())
                        .branch(structureService.getBranch(branchId))
                        .vsNames(Collections.emptyList())
                        .build()
        );
    }

    // FIXME Optional<ValidationStampFilter> getValidationStampFilterByName(Branch branch, String name);

    @GetMapping("/global/create")
    public Form getNewGlobalValidationStampFilterForm() {
        return Form.create()
                .name()
                .with(
                        MultiStrings.of("patterns")
                                .label("List of validation stamp patterns")
                );
    }

    // FIXME ValidationStampFilter newValidationStampFilter(ValidationStampFilter filter);

    // FIXME void saveValidationStampFilter(ValidationStampFilter filter);

    // FIXME Ack deleteValidationStampFilter(ValidationStampFilter filter);

    // FIXME ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter, Project project);

    // FIXME ValidationStampFilter shareValidationStampFilter(ValidationStampFilter filter);

    // FIXME ValidationStampFilter getValidationStampFilter(ID id);
}
