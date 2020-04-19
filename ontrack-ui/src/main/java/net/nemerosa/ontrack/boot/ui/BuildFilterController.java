package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterForm;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterInput;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResource;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Management of build filters for the branches.
 */
@RestController
@RequestMapping("rest")
public class BuildFilterController extends AbstractResourceController {

    private final BuildFilterService buildFilterService;

    @Autowired
    public BuildFilterController(BuildFilterService buildFilterService) {
        this.buildFilterService = buildFilterService;
    }

    /**
     * Returns the list of existing filters for this branch and the current user.
     *
     * @param branchId ID of the branch to get the filter for.
     */
    @RequestMapping(value = "branches/{branchId}/filters", method = RequestMethod.GET)
    public Resources<BuildFilterResource<?>> buildFilters(@PathVariable ID branchId) {
        return Resources.of(
                buildFilterService.getBuildFilters(branchId),
                uri(on(getClass()).buildFilters(branchId))
        );
    }

    /**
     * Returns the list of forms to create new forms.
     *
     * @param branchId ID of the branch to get the filter forms for.
     */
    @RequestMapping(value = "branches/{branchId}/filters/forms", method = RequestMethod.GET)
    public Resources<BuildFilterForm> buildFilterForms(@PathVariable ID branchId) {
        return Resources.of(
                buildFilterService.getBuildFilterForms(branchId),
                uri(on(getClass()).buildFilterForms(branchId))
        );
    }

    /**
     * Getting the edition form for a filter
     */
    @RequestMapping(value = "branches/{branchId}/filters/{name}", method = RequestMethod.GET)
    public Resource<BuildFilterForm> getEditionForm(@PathVariable ID branchId, @PathVariable String name) {
        BuildFilterForm editionForm = buildFilterService.getEditionForm(branchId, name);
        return Resource.of(
                editionForm,
                uri(on(getClass()).getEditionForm(branchId, name))
        ).with(Link.UPDATE, uri(on(getClass()).saveFilter(
                branchId,
                name,
                null
        )));
    }

    /**
     * Creating a filter
     */
    @RequestMapping(value = "branches/{branchId}/filters", method = RequestMethod.POST)
    public Ack createFilter(@PathVariable ID branchId, @RequestBody @Valid BuildFilterInput input) {
        return buildFilterService.saveFilter(branchId, input.isShared(), input.getName(), input.getType(), input.getData());
    }

    /**
     * Saving a filter
     */
    @RequestMapping(value = "branches/{branchId}/filters/{name}", method = RequestMethod.PUT)
    public Ack saveFilter(@PathVariable ID branchId, @PathVariable String name, @RequestBody @Valid BuildFilterInput input) {
        if (!StringUtils.equals(name, input.getName())) {
            throw new IllegalArgumentException("The input name must be identical to the one in the URI.");
        }
        return buildFilterService.saveFilter(branchId, input.isShared(), name, input.getType(), input.getData());
    }

    /**
     * Deletes a filter
     */
    @RequestMapping(value = "branches/{branchId}/filters/{name}", method = RequestMethod.DELETE)
    public Ack deleteFilter(@PathVariable ID branchId, @PathVariable String name) {
        return buildFilterService.deleteFilter(branchId, name);
    }
}
