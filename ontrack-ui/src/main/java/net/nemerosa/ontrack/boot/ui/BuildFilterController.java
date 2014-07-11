package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResource;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import static net.nemerosa.ontrack.boot.ui.UIUtils.requestParametersToJson;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Management of build filters for the branches.
 */
@RestController
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
     * Saving a filter
     */
    @RequestMapping(value = "branches/{branchId}/filters/{name}/{type}", method = RequestMethod.PUT)
    public Ack saveFilter(@PathVariable ID branchId, @PathVariable String name, @PathVariable String type, WebRequest request) {
        return buildFilterService.saveFilter(branchId, name, type, requestParametersToJson(request));
    }

    /**
     * Deletes a filter
     */
    @RequestMapping(value = "branches/{branchId}/filters/{name}", method = RequestMethod.DELETE)
    public Ack deleteFilter(@PathVariable ID branchId, @PathVariable String name) {
        return buildFilterService.deleteFilter(branchId, name);
    }
}
